        function encargadoPage() {
            return {
                currentUser: '',
                ordenes: [],
                paginaPorEstado: {},
                elementosPorPagina: 4,
                empleados: [],
                servicios: [],
                estados: [],
                globalError: '',
                globalSuccess: '',
                confirmandoOrdenes: {},
                
                paginaServicios: 1,
                serviciosPorPagina: 4,
                
                modal: { show: false, loading: false, error: '' },
                modalOrden: { show: false, orden: null },
                modalRevision: { show: false, orden: null, descripcion: '', loading: false, error: '' },
                
                view: 'ordenes',
                searchQuery: '',

                personal: [],
                clientes: [],
                searchVehiculoDoc: '',
                searchVehiculoPlaca: '',
                vehiculoBuscado: null,
                searchIntento: false,
                clienteEncontradoVehiculos: null,
                vehiculosDelCliente: [],
                historialOrdenesVehiculo: [],
                historialPage: 0,
                historialTotalPages: 0,
                modalVehiculo: { show: false, mode: 'create', loading: false, error: '' },
                formVehiculoGlobal: { placa: '', marca: '', modelo: '', color: '', year: '', tipoVehiculo: { id: '' }, activo: true },
                
                orderForm: {
                    clienteDoc: '',
                    cliente: null,
                    placa: '',
                    empleadoId: '',
                    serviciosSeleccionados: []
                },
                
                vehiculosCliente: [],
                tiposVehiculo: [],
                mostrarRegistroVehiculo: false,
                formVehiculo: { placa: '', marca: '', modelo: '', color: '', year: '', tipoVehiculo: { id: '' } },

                async init() {
                    try {
                        const res = await fetch('/api/auth/me', { credentials: 'include' });
                        if (!res.ok) {
                            window.location.href = '/';
                            return;
                        }
                        const data = await res.json();
                        if (data.rol !== 'ENCARGADO') {
                            window.location.href = '/';
                            return;
                        }
                        sessionStorage.setItem('rol', data.rol);
                        this.currentUser = 'Encargado';
                        if (!sessionStorage.getItem('encargadoSessionStart')) {
                            sessionStorage.setItem('encargadoSessionStart', new Date().toISOString());
                        }
                        this.cargarDatosIniciales();
                    } catch (e) {
                        window.location.href = '/';
                    }
                },

                getHeaders() {
                    return {
                        'Content-Type': 'application/json'
                    };
                },

                async cargarDatosIniciales() {
                    try {
                        
                        const empRes = await fetch('/api/personal/empleados', { headers: this.getHeaders(), credentials: 'include' });
                        if(empRes.ok) {
                            const emps = await empRes.json();
                            this.empleados = emps.filter(e => e.disponibleHoy && e.activo);
                        }

                        
                        const srvRes = await fetch('/api/servicios', { headers: this.getHeaders(), credentials: 'include' });
                        if(srvRes.ok) {
                            this.servicios = await srvRes.json();
                        }

                        
                        const tvRes = await fetch('/api/tipovehiculo', { headers: this.getHeaders(), credentials: 'include' });
                        if(tvRes.ok) {
                            this.tiposVehiculo = await tvRes.json();
                        }

                        
                        const estRes = await fetch('/api/estados', { headers: this.getHeaders(), credentials: 'include' });
                        if(estRes.ok) {
                            this.estados = await estRes.json();
                        }

                        await this.cargarOrdenes();
                        await this.cargarDatosBase();
                    } catch(e) {
                        this.globalError = 'Error conectando con el servidor.';
                    }
                },

                
                modalCrud: { show: false, mode: 'create', loading: false, error: '' },
                formCrud: { userCode: '', nombre: '', apellido: '', documento: '', telefono: '', correo: '', direccion: '', password: '', activo: true, disponibleHoy: true },
                
                async cargarDatosBase() {
                    try {
                        const [resE, resC] = await Promise.all([
                            fetch('/api/personal/empleados', { headers: this.getHeaders(), credentials: 'include' }),
                            fetch('/api/clientes', { headers: this.getHeaders(), credentials: 'include' })
                        ]);
                        if(resE.ok) this.personal = await resE.json();
                        if(resC.ok) this.clientes = await resC.json();
                    } catch(e) {}
                },
                
                getFilteredData() {
                    let data = this.view === 'clientes' ? this.clientes : this.personal;
                    if(!this.searchQuery) return data;
                    const q = this.searchQuery.toLowerCase();
                    return data.filter(p => 
                        (p.documento && p.documento.toLowerCase().includes(q)) ||
                        (p.correo && p.correo.toLowerCase().includes(q))
                    );
                },

                openCrudModal(mode, data = null) {
                    this.modalCrud.mode = mode;
                    this.modalCrud.error = '';
                    if(mode === 'create') {
                        this.formCrud = { userCode: '', nombre: '', apellido: '', documento: '', telefono: '', correo: '', direccion: '', password: '', activo: true, disponibleHoy: true };
                    } else {
                        this.formCrud = { ...data, password: '', rol: (data.roles && data.roles.length > 0) ? data.roles[0] : 'EMPLEADO' };
                    }
                    this.modalCrud.show = true;
                },

                async submitCrudForm() {
                    if (this.modalCrud.loading) return;
                    this.modalCrud.loading = true;
                    this.modalCrud.error = '';
                    try {
                        const isC = this.view === 'clientes';
                        const url = isC ? (this.modalCrud.mode==='create' ? '/api/clientes' : `/api/clientes/${this.formCrud.userCode}`) : (this.modalCrud.mode==='create' ? '/api/personal' : `/api/personal/${this.formCrud.userCode}`);
                        const method = this.modalCrud.mode==='create' ? 'POST' : 'PUT';
                        const body = { ...this.formCrud };
                        if(!isC && this.modalCrud.mode==='create') body.rol = 'EMPLEADO';
                        if(!isC && this.modalCrud.mode==='edit' && !body.rol) body.rol = 'EMPLEADO';
                        
                        const res = await fetch(url, { method, headers: this.getHeaders(), credentials: 'include', body: JSON.stringify(body) });
                        if(!res.ok) {
                            const errText = await res.text();
                            throw new Error(`Error: ${errText}`);
                        }
                        
                        this.globalSuccess = 'Guardado con éxito';
                        setTimeout(()=>this.globalSuccess='', 3000);
                        this.modalCrud.show = false;
                        this.cargarDatosBase();
                    } catch(e) { this.modalCrud.error = e.message; }
                    finally { this.modalCrud.loading = false; }
                },
                
                async eliminarPersona(p) {
                    if(!confirm(`⚠️ Advertencia: Esta acción borrará a ${p.nombre} permanentemente y podría afectar el historial de reportes.\n\nSi la persona ya no trabaja aquí o ya no es cliente, se recomienda cambiar su estado a Inactivo editando su perfil.\n\n¿Deseas eliminarlo de todos modos?`)) return;
                    const isC = this.view === 'clientes';
                    const url = isC ? `/api/clientes/${p.userCode}` : `/api/personal/${p.userCode}`;
                    await fetch(url, { method: 'DELETE', headers: this.getHeaders(), credentials: 'include' });
                },

                async buscarVehiculoEspecifico() {
                    this.vehiculoBuscado = null;
                    this.clienteEncontradoVehiculos = null;
                    this.vehiculosDelCliente = [];
                    this.historialOrdenesVehiculo = [];
                    this.searchIntento = true;
                    
                    if(!this.searchVehiculoDoc && !this.searchVehiculoPlaca) {
                        return alert('Debe ingresar el documento del cliente o la placa del vehículo.');
                    }
                    
                    try {
                        if (this.searchVehiculoPlaca) {
                            const res = await fetch(`/api/vehiculos/${this.searchVehiculoPlaca}`, { headers: this.getHeaders(), credentials: 'include' });
                            if(res.ok) {
                                this.vehiculoBuscado = await res.json();
                                this.clienteEncontradoVehiculos = this.clientes.find(c => c.id === this.vehiculoBuscado.clienteId);
                                this.vehiculosDelCliente = [this.vehiculoBuscado];
                                this.historialPage = 0;
                                await this.cargarHistorialVehiculo(0);
                            } else {
                                alert('No se encontró ningún vehículo con esa placa.');
                            }
                        } else if (this.searchVehiculoDoc) {
                            this.clienteEncontradoVehiculos = this.clientes.find(c => c.documento === this.searchVehiculoDoc);
                            if(!this.clienteEncontradoVehiculos) return alert('Cliente no encontrado en el sistema.');
                            
                            const res = await fetch(`/api/vehiculos/cliente/${this.clienteEncontradoVehiculos.userCode}`, { headers: this.getHeaders(), credentials: 'include' });
                            if(res.ok) {
                                this.vehiculosDelCliente = await res.json();
                                if(this.vehiculosDelCliente.length === 0) {
                                    alert('El cliente no tiene vehículos registrados.');
                                }
                            }
                        }
                    } catch(e) {}
                },

                async cargarHistorialVehiculo(page) {
                    if(!this.vehiculoBuscado) return;
                    try {
                        const resHist = await fetch(`/api/ordenes/vehiculo/${this.vehiculoBuscado.placa}?page=${page}&size=5&sort=fechaInicio,desc`, { headers: this.getHeaders(), credentials: 'include' });
                        if (resHist.ok) {
                            const pageData = await resHist.json();
                            this.historialOrdenesVehiculo = pageData.content || [];
                            this.historialPage = pageData.number || 0;
                            this.historialTotalPages = pageData.totalPages || 0;
                        }
                    } catch(e) {}
                },

                openModalVehiculo(mode, veh) {
                    this.modalVehiculo.mode = mode;
                    this.formVehiculoGlobal = { ...veh, tipoVehiculo: { id: veh.tipoVehiculo?.id } };
                    this.modalVehiculo.show = true;
                },

                async submitVehiculoGlobal() {
                    if (this.modalVehiculo.loading) return;
                    this.modalVehiculo.loading = true;
                    try {
                        const url = `/api/vehiculos/${this.formVehiculoGlobal.placa}`;
                        const res = await fetch(url, { method: 'PUT', headers: this.getHeaders(), credentials: 'include', body: JSON.stringify(this.formVehiculoGlobal) });
                        if(!res.ok) throw new Error('Error al guardar');
                        this.modalVehiculo.show = false;
                        this.buscarVehiculoEspecifico();
                    } catch(e) { this.modalVehiculo.error = e.message; }
                    finally { this.modalVehiculo.loading = false; }
                },
                
                async eliminarVehiculoGlobal(placa) {
                    if(!confirm('¿Eliminar vehículo?')) return;
                    await fetch(`/api/vehiculos/${placa}`, { method: 'DELETE', headers: this.getHeaders(), credentials: 'include' });
                    this.buscarVehiculoEspecifico();
                },

                async cargarOrdenes() {
                    try {
                        const t = Date.now();
                        const res = await fetch(`/api/ordenes?size=1000&sort=fechaInicio,desc&_t=${t}`, { headers: this.getHeaders(), credentials: 'include' });
                        if(res.ok) {
                            const data = await res.json();
                            this.ordenes = Array.isArray(data) ? data : (data.content || []);
                        }
                    } catch(e) { console.error(e); }
                },

                ordenesPorEstado(nombreEstado) {
                    const estadoConfig = this.estados.find(e => e.nombre === nombreEstado);
                    let list = this.ordenes.filter(o => o.estado === nombreEstado);
                    
                    if (nombreEstado === 'CON_PROBLEMAS') {
                        list = list.filter(o => !o.fechaFin);
                    } else if (estadoConfig && estadoConfig.esFinal) {
                        const sessionStartStr = sessionStorage.getItem('encargadoSessionStart');
                        if (!sessionStartStr) return [];
                        const sessionStart = new Date(sessionStartStr);
                        list = list.filter(o => {
                            if (!o.fechaFin) return false;
                            const d = this.parseDate(o.fechaFin);
                            return d && d >= sessionStart;
                        });
                        
                        return list.sort((a,b) => {
                            const d1 = this.parseDate(a.fechaFin);
                            const d2 = this.parseDate(b.fechaFin);
                            return (d2 ? d2.getTime() : 0) - (d1 ? d1.getTime() : 0);
                        });
                    }
                    
                    
                    
                    return list.sort((a,b) => {
                        const d1 = this.parseDate(a.fechaInicio);
                        const d2 = this.parseDate(b.fechaInicio);
                        return (d1 ? d1.getTime() : 0) - (d2 ? d2.getTime() : 0);
                    });
                },

                obtenerPagina(estado) {
                    return this.paginaPorEstado[estado] || 1;
                },
                
                cambiarPagina(estado, delta) {
                    const actual = this.obtenerPagina(estado);
                    this.paginaPorEstado[estado] = actual + delta;
                },
                
                ordenesPaginadasPorEstado(nombreEstado) {
                    const list = this.ordenesPorEstado(nombreEstado);
                    const pagina = this.obtenerPagina(nombreEstado);
                    const start = (pagina - 1) * this.elementosPorPagina;
                    
                    if (start >= list.length && list.length > 0) {
                        this.paginaPorEstado[nombreEstado] = Math.ceil(list.length / this.elementosPorPagina);
                        return list.slice((this.paginaPorEstado[nombreEstado] - 1) * this.elementosPorPagina, this.paginaPorEstado[nombreEstado] * this.elementosPorPagina);
                    }
                    return list.slice(start, start + this.elementosPorPagina);
                },
                
                hayPaginaAnterior(nombreEstado) {
                    return this.obtenerPagina(nombreEstado) > 1;
                },
                
                hayPaginaSiguiente(nombreEstado) {
                    const list = this.ordenesPorEstado(nombreEstado);
                    const pagina = this.obtenerPagina(nombreEstado);
                    return list.length > pagina * this.elementosPorPagina;
                },

                verDetallesOrden(orden) {
                    this.modalOrden.orden = orden;
                    this.modalOrden.show = true;
                },

                parseDate(dateInput) {
                    if (!dateInput) return null;
                    if (Array.isArray(dateInput)) {
                        return new Date(dateInput[0], dateInput[1] - 1, dateInput[2], dateInput[3] || 0, dateInput[4] || 0, dateInput[5] || 0);
                    }
                    return new Date(dateInput);
                },

                formatTime(dateInput) {
                    const d = this.parseDate(dateInput);
                    return d ? d.toLocaleTimeString() : 'N/A';
                },

                formatDateTime(dateInput) {
                    const d = this.parseDate(dateInput);
                    return d ? d.toLocaleString() : 'N/A';
                },

                getNombreEmpleado(personalId) {
                    if (!this.personal || this.personal.length === 0) return personalId;
                    const emp = this.personal.find(p => p.userCode === personalId);
                    return emp ? `${emp.nombre} ${emp.apellido}` : personalId;
                },

                getNombreCliente(clienteId) {
                    if (!this.clientes || this.clientes.length === 0) return clienteId;
                    const cli = this.clientes.find(c => c.userCode === clienteId);
                    return cli ? `${cli.nombre} ${cli.apellido}` : clienteId;
                },

                ordenesDelTurno() {
                    const sessionStartStr = sessionStorage.getItem('encargadoSessionStart');
                    if (!sessionStartStr) return [];
                    const sessionStart = new Date(sessionStartStr);
                    
                    return this.ordenes.filter(o => {
                        if(o.estado !== 'FINALIZADO' && o.estado !== 'CONFIRMADO') return false;
                        if(!o.fechaFin) return false;
                        const d = this.parseDate(o.fechaFin);
                        return d && d >= sessionStart;
                    });
                },

                canceladasPurasDelTurno() {
                    const sessionStartStr = sessionStorage.getItem('encargadoSessionStart');
                    if (!sessionStartStr) return [];
                    const sessionStart = new Date(sessionStartStr);
                    
                    return this.ordenes.filter(o => {
                        if(o.estado !== 'CANCELADO') return false;
                        if(!o.fechaFin) return false;
                        const d = this.parseDate(o.fechaFin);
                        return d && d >= sessionStart;
                    });
                },

                incidenciasCerradasDelTurno() {
                    const sessionStartStr = sessionStorage.getItem('encargadoSessionStart');
                    if (!sessionStartStr) return [];
                    const sessionStart = new Date(sessionStartStr);
                    
                    return this.ordenes.filter(o => {
                        if(o.estado !== 'CON_PROBLEMAS') return false;
                        if(!o.fechaFin) return false;
                        const d = this.parseDate(o.fechaFin);
                        return d && d >= sessionStart;
                    });
                },

                canceladasDelTurno() {
                    return [...this.canceladasPurasDelTurno(), ...this.incidenciasCerradasDelTurno()];
                },

                totalDelTurno() {
                    return this.ordenesDelTurno().reduce((acc, o) => acc + o.total, 0).toLocaleString();
                },

                openModal() {
                    this.orderForm = { clienteDoc: '', cliente: null, placa: '', empleadoId: '', serviciosSeleccionados: [] };
                    this.vehiculosCliente = [];
                    this.mostrarRegistroVehiculo = false;
                    this.modal.error = '';
                    this.paginaServicios = 1;
                    this.modal.show = true;
                },
                closeModal() { this.modal.show = false; },

                serviciosPaginados() {
                    const start = (this.paginaServicios - 1) * this.serviciosPorPagina;
                    return this.servicios.slice(start, start + this.serviciosPorPagina);
                },

                cambiarPaginaServicios(delta) {
                    this.paginaServicios += delta;
                },

                async buscarCliente() {
                    if (this.modal.loading) return;
                    this.modal.loading = true;
                    this.modal.error = '';
                    try {
                        const res = await fetch(`/api/clientes/documento/${this.orderForm.clienteDoc}`, { headers: this.getHeaders(), credentials: 'include' });
                        if (res.ok) {
                            const encontrado = await res.json();
                            this.orderForm.cliente = encontrado;
                            await this.cargarVehiculosCliente(encontrado.userCode);
                        } else {
                            this.modal.error = 'Cliente o miembro del personal no encontrado. Registre la persona primero.';
                            this.orderForm.cliente = null;
                            this.vehiculosCliente = [];
                        }
                    } catch(e) {
                        this.modal.error = 'Error buscando cliente';
                    } finally {
                        this.modal.loading = false;
                    }
                },

                async cargarVehiculosCliente(userCode) {
                    try {
                        const res = await fetch(`/api/vehiculos/cliente/${userCode}`, { headers: this.getHeaders(), credentials: 'include' });
                        if(res.ok) {
                            this.vehiculosCliente = await res.json();
                        }
                    } catch(e) { console.error('Error cargando vehículos'); }
                },

                async registrarVehiculo() {
                    if(!this.formVehiculo.placa || !this.formVehiculo.tipoVehiculo.id) {
                        this.modal.error = 'Llene al menos placa y tipo de vehículo.';
                        return;
                    }
                    if (this.modal.loading) return;
                    this.modal.loading = true;
                    this.modal.error = '';
                    try {
                        const payload = { ...this.formVehiculo, documentoCliente: this.orderForm.cliente.documento };
                        const res = await fetch('/api/vehiculos', {
                            method: 'POST',
                            headers: this.getHeaders(),
                            credentials: 'include',
                            body: JSON.stringify(payload)
                        });
                        if(!res.ok) {
                            const errData = await res.json().catch(()=>({}));
                            throw new Error(errData.message || errData.error || 'Error al registrar vehículo');
                        }
                        await this.cargarVehiculosCliente(this.orderForm.cliente.userCode);
                        this.orderForm.placa = this.formVehiculo.placa.toUpperCase();
                        this.mostrarRegistroVehiculo = false;
                        this.formVehiculo = { placa: '', marca: '', modelo: '', color: '', year: '', tipoVehiculo: { id: '' } };
                    } catch(e) {
                        this.modal.error = e.message;
                    } finally {
                        this.modal.loading = false;
                    }
                },

                isOrderValid() {
                    if (!this.orderForm.cliente || !this.orderForm.empleadoId || this.orderForm.serviciosSeleccionados.length === 0) return false;
                    if (this.mostrarRegistroVehiculo) return true;
                    if (!this.orderForm.placa) return false;
                    return this.vehiculosCliente.some(v => v.placa.toUpperCase() === this.orderForm.placa.toUpperCase());
                },

                async submitOrder() {
                    if (this.modal.loading) return;
                    this.modal.loading = true;
                    this.modal.error = '';
                    try {
                        const payload = {
                            vehiculoId: this.orderForm.placa, 
                            personalId: this.orderForm.empleadoId,
                            clienteId: this.orderForm.cliente.userCode,
                            serviceCode: this.orderForm.serviciosSeleccionados,
                            fechaInicio: new Date().toISOString()
                        };

                        const res = await fetch('/api/ordenes', {
                            method: 'POST',
                            headers: this.getHeaders(),
                            credentials: 'include',
                            body: JSON.stringify(payload)
                        });

                        if(!res.ok) {
                            let errorMsg = 'Error al crear la orden.';
                            try {
                                const errData = await res.json();
                                errorMsg = errData.message || errData.error || errData.details || errorMsg;
                            } catch(e) {
                                errorMsg = await res.text();
                            }
                            throw new Error(errorMsg);
                        }

                        this.globalSuccess = 'Orden de lavado creada con éxito.';
                        setTimeout(() => this.globalSuccess = '', 3000);
                        this.closeModal();
                        this.cargarOrdenes();
                    } catch(e) {
                        this.modal.error = e.message;
                    } finally {
                        this.modal.loading = false;
                    }
                },

                async confirmarOrden(id) {
                    console.log("Click en confirmarOrden para la orden:", id);
                    if (this.confirmandoOrdenes[id]) {
                        console.log("Orden ya se está confirmando, ignorando click duplicado:", id);
                        return; 
                    }
                    console.log("Estableciendo confirmandoOrdenes para la orden:", id);
                    this.confirmandoOrdenes = { ...this.confirmandoOrdenes, [id]: true };
                    try {
                        console.log("Realizando fetch PATCH a /api/ordenes/" + id + "/estado");
                        const res = await fetch(`/api/ordenes/${id}/estado`, {
                            method: 'PATCH',
                            headers: this.getHeaders(),
                            credentials: 'include',
                            body: JSON.stringify({ estado: 'FINALIZADO', fechaFin: new Date().toISOString() })
                        });
                        
                        if(!res.ok) {
                            const errorText = await res.text();
                            console.error("Error devuelto por la API:", errorText);
                            throw new Error(`Error: ${errorText}`);
                        }
                        console.log("Estado de orden actualizado con éxito.");
                        this.globalSuccess = 'Lavado aprobado y cliente notificado.';
                        setTimeout(() => this.globalSuccess = '', 3000);
                        this.cargarOrdenes();
                    } catch(e) {
                        console.error("Error al confirmar orden:", e);
                        this.globalError = e.message;
                        setTimeout(() => this.globalError = '', 4000);
                        
                        console.log("Restaurando estado habilitado por error para la orden:", id);
                        const copy = { ...this.confirmandoOrdenes };
                        delete copy[id];
                        this.confirmandoOrdenes = copy;
                    }
                },
                
                abrirModalRevision(orden) {
                    this.modalRevision.orden = orden;
                    this.modalRevision.descripcion = orden.descripcionProblema || '';
                    this.modalRevision.error = '';
                    this.modalRevision.show = true;
                },
                
                async submitRevision() {
                    if (this.modalRevision.loading) return;
                    this.modalRevision.loading = true;
                    this.modalRevision.error = '';
                    try {
                        const res = await fetch(`/api/ordenes/${this.modalRevision.orden.ordenCode}/estado`, {
                            method: 'PATCH',
                            headers: this.getHeaders(),
                            credentials: 'include',
                            body: JSON.stringify({ 
                                estado: 'CON_PROBLEMAS',
                                descripcionProblema: this.modalRevision.descripcion,
                                fechaFin: new Date().toISOString()
                            })
                        });
                        
                        if(!res.ok) {
                            let errorMsg = 'Error al actualizar el problema';
                            try { const errData = await res.json(); errorMsg = errData.message || errData.error || errorMsg; } catch(e) { errorMsg = await res.text(); }
                            throw new Error(errorMsg);
                        }
                        
                        this.globalSuccess = 'Notificación enviada y orden cancelada.';
                        setTimeout(() => this.globalSuccess = '', 4000);
                        this.modalRevision.show = false;
                        this.cargarOrdenes();
                    } catch(e) {
                        this.modalRevision.error = e.message;
                    } finally {
                        this.modalRevision.loading = false;
                    }
                },

                async cancelarOrden(id) {
                    if(!confirm('¿Estás seguro de que deseas cancelar esta orden?')) return;
                    try {
                        const res = await fetch(`/api/ordenes/${id}/estado`, {
                            method: 'PATCH',
                            headers: this.getHeaders(),
                            credentials: 'include',
                            body: JSON.stringify({ estado: 'CANCELADO', fechaFin: new Date().toISOString() })
                        });
                        
                        if(!res.ok) {
                            const errorText = await res.text();
                            throw new Error(`Error al cancelar: ${errorText}`);
                        }
                        this.globalSuccess = 'Orden cancelada con éxito.';
                        setTimeout(() => this.globalSuccess = '', 3000);
                        this.cargarOrdenes();
                    } catch(e) {
                        this.globalError = e.message;
                    }
                },

                async logout() {
                    const confirmMessage = `Resumen de tu turno:\nLavados completados: ${this.ordenesDelTurno().length}\nIncidencias ocurridas: ${this.incidenciasCerradasDelTurno().length}\nÓrdenes canceladas: ${this.canceladasPurasDelTurno().length}\nTotal recaudado: $${this.totalDelTurno()}\n\n¿Deseas cerrar tu sesión?`;
                    if(!confirm(confirmMessage)) return;

                    try {
                        await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
                    } catch (e) {}
                    sessionStorage.removeItem('rol');
                    sessionStorage.removeItem('encargadoSessionStart');
                    window.location.href = '/';
                }
            }
        }
        window.encargadoPage = encargadoPage;

