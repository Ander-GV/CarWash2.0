function adminPage() { return { personal: [], clientes: [], servicios: [], tiposVehiculo: [],
            activeTab: 'ADMIN',
            loading: true,
            globalError: '',
            globalSuccess: '',

            searchVehiculoDoc: '',
            searchVehiculoPlaca: '',
            vehiculoBuscado: null,
            searchIntento: false,
            clienteEncontradoVehiculos: null,
            vehiculosDelCliente: [],
            historialOrdenesVehiculo: [],
            historialPage: 0,
            historialTotalPages: 0,
            modalVehiculoGlobal: { show: false, mode: 'create', loading: false, error: '' },
            formVehiculoGlobal: { placa: '', marca: '', modelo: '', color: '', year: '', tipoVehiculo: { id: '' }, activo: true },

            searchQuery: '',
            currentUser: '',

            modal: {
                show: false,
                mode: 'create',
                loading: false,
                error: '',
                isCliente: false
            },

            modalServicio: {
                show: false,
                mode: 'create',
                loading: false,
                error: ''
            },

            modalTipoVehiculo: {
                show: false,
                loading: false,
                error: ''
            },

            showPassword: false,

            form: {
                userCode: '',
                nombre: '',
                apellido: '',
                documento: '',
                telefono: '',
                correo: '',
                direccion: '',
                password: '',
                rol: '',
                activo: true,
                disponibleHoy: true
            },

            formServicio: {
                serviceCode: '',
                nombre: '',
                descripcion: '',
                precios: []
            },

            formTipoVehiculo: {
                nombre: ''
            },

            async init() {
                try {
                    const res = await fetch('/api/auth/me', { credentials: 'include' });
                    if (!res.ok) {
                        window.location.href = '/';
                        return;
                    }
                    const data = await res.json();
                    if (data.rol !== 'ADMIN') {
                        window.location.href = '/';
                        return;
                    }
                    sessionStorage.setItem('rol', data.rol);
                    this.currentUser = 'Administrador';
                    this.cargarDatos();
                } catch (e) {
                    window.location.href = '/';
                }
            },

            getHeaders() {
                return {
                    'Content-Type': 'application/json'
                };
            },

            async cargarDatos() {
                this.loading = true;
                this.globalError = '';
                try {
                    const [resAdmin, resEncargado, resEmpleado, resClientes, resServicios, resTipos] = await Promise.all([
                        fetch('/api/personal/admin', { headers: this.getHeaders(), credentials: 'include', cache: 'no-store' }),
                        fetch('/api/personal/encargado', { headers: this.getHeaders(), credentials: 'include', cache: 'no-store' }),
                        fetch('/api/personal/empleados', { headers: this.getHeaders(), credentials: 'include', cache: 'no-store' }),
                        fetch('/api/clientes', { headers: this.getHeaders(), credentials: 'include', cache: 'no-store' }),
                        fetch('/api/servicios/todos', { headers: this.getHeaders(), credentials: 'include', cache: 'no-store' }),
                        fetch('/api/tipovehiculo/todos', { headers: this.getHeaders(), credentials: 'include', cache: 'no-store' })
                    ]);

                    if (!resAdmin.ok && resAdmin.status === 401) throw new Error('401');

                    const [admins, encargados, empleados, clientes, servicios, tipos] = await Promise.all([
                        resAdmin.ok ? resAdmin.json() : [],
                        resEncargado.ok ? resEncargado.json() : [],
                        resEmpleado.ok ? resEmpleado.json() : [],
                        resClientes.ok ? resClientes.json() : [],
                        resServicios.ok ? resServicios.json() : [],
                        resTipos.ok ? resTipos.json() : []
                    ]);

                    this.personal = [...admins, ...encargados, ...empleados];
                    this.clientes = clientes;
                    this.servicios = servicios;
                    this.tiposVehiculo = tipos;
                } catch (e) {
                    this.globalError = 'Error al cargar los datos.';
                    if (e.message === '401') {
                        this.logout();
                    }
                } finally {
                    this.loading = false;
                }
            },

            tiposVehiculoActivos() {
                return this.tiposVehiculo.filter(tv => tv.activo);
            },

            getFilteredData() {
                let data = [];
                if (this.activeTab === 'CLIENTE') {
                    data = this.clientes;
                } else if (this.activeTab === 'SERVICIO') {
                    data = this.servicios;
                } else if (this.activeTab === 'TIPO_VEHICULO') {
                    data = this.tiposVehiculo;
                } else {
                    data = this.personal.filter(p => p.rol === this.activeTab);
                }

                if (this.searchQuery.trim() === '') return data;

                const q = this.searchQuery.toLowerCase();
                return data.filter(p => {
                    if (this.activeTab === 'SERVICIO') {
                        return (p.nombre && p.nombre.toLowerCase().includes(q)) ||
                            (p.serviceCode && p.serviceCode.toLowerCase().includes(q));
                    }
                    if (this.activeTab === 'TIPO_VEHICULO') {
                        return (p.nombre && p.nombre.toLowerCase().includes(q));
                    }
                    return (p.documento && p.documento.toLowerCase().includes(q)) ||
                        (p.userCode && p.userCode.toLowerCase().includes(q)) ||
                        (p.correo && p.correo.toLowerCase().includes(q));
                });
            },

            openModal(mode, data = null) {
                this.modal.mode = mode;
                this.modal.error = '';
                this.showPassword = false;
                this.modal.isCliente = this.activeTab === 'CLIENTE';

                if (mode === 'create') {
                    this.form = {
                        userCode: '', nombre: '', apellido: '', documento: '',
                        telefono: '', correo: '', direccion: '', password: '',
                        rol: this.modal.isCliente ? '' : this.activeTab,
                        activo: true, disponibleHoy: true
                    };
                } else {
                    this.form = {
                        userCode: data.userCode,
                        nombre: data.nombre || '',
                        apellido: data.apellido || '',
                        documento: data.documento || '',
                        telefono: data.telefono || '',
                        correo: data.correo || '',
                        direccion: data.direccion || '',
                        password: '',
                        rol: (data.roles && data.roles.length > 0) ? data.roles[0] : (this.modal.isCliente ? '' : this.activeTab),
                        activo: data.activo !== false,
                        disponibleHoy: data.disponibleHoy === true
                    };
                }
                this.modal.show = true;
            },

            openModalServicio(mode, data = null) {
                this.modalServicio.mode = mode;
                this.modalServicio.error = '';
                if (mode === 'create') {
                    this.formServicio = { serviceCode: '', nombre: '', descripcion: '', precios: [] };
                } else {
                    this.formServicio = {
                        serviceCode: data.serviceCode,
                        nombre: data.nombre,
                        descripcion: data.descripcion,
                        precios: data.precios ? JSON.parse(JSON.stringify(data.precios)) : []
                    };
                }
                this.modalServicio.show = true;
            },

            addPrecioOption() {
                this.formServicio.precios.push({ tipoVehiculoId: '', nombre: '', precio: 0 });
            },

            removePrecioOption(index) {
                this.formServicio.precios.splice(index, 1);
            },

            closeModal() {
                this.modal.show = false;
            },

            async submitForm() {
                this.modal.error = '';
                this.modal.loading = true;

                try {
                    const isCreate = this.modal.mode === 'create';
                    const isCliente = this.modal.isCliente;

                    let url = '';
                    if (isCliente) {
                        url = isCreate ? '/api/clientes' : `/api/clientes/${this.form.userCode}`;
                    } else {
                        url = isCreate ? '/api/personal' : `/api/personal/${this.form.userCode}`;
                    }

                    const method = isCreate ? 'POST' : 'PUT';

                    const bodyData = {
                        nombre: this.form.nombre,
                        apellido: this.form.apellido,
                        telefono: this.form.telefono,
                        correo: this.form.correo,
                        direccion: this.form.direccion,
                        activo: this.form.activo
                    };

                    if (isCreate || isCliente) {
                        bodyData.documento = this.form.documento;
                    }

                    if (!isCliente) {
                        bodyData.rol = this.form.rol;
                        bodyData.disponibleHoy = this.form.disponibleHoy;
                        if (isCreate) {
                            bodyData.password = this.form.password;
                        } else {
                            bodyData.password = this.form.password || "";
                        }
                    }

                    const resp = await fetch(url, {
                        method: method,
                        headers: this.getHeaders(),
                        credentials: 'include',
                        body: JSON.stringify(bodyData)
                    });

                    if (!resp.ok) {
                        const errData = await resp.json().catch(() => ({}));
                        let errMsg = errData.message || errData.error || 'Ocurrió un error al guardar';
                        throw new Error(errMsg);
                    }



                    this.globalSuccess = `${isCliente ? 'Cliente' : 'Personal'} ${isCreate ? 'creado' : 'actualizado'} con éxito.`;
                    setTimeout(() => this.globalSuccess = '', 3000);

                    if (!isCliente && this.form.rol) {
                        this.activeTab = this.form.rol;
                    } else if (isCliente) {
                        this.activeTab = 'CLIENTE';
                    }

                    this.closeModal();
                    await this.cargarDatos();
                } catch (e) {
                    this.modal.error = e.message;
                } finally {
                    this.modal.loading = false;
                }
            },

            async eliminar(persona) {
                const isCliente = this.activeTab === 'CLIENTE';
                if (!confirm(`⚠️ Advertencia: Esta acción borrará a ${persona.nombre} ${persona.apellido} permanentemente y podría afectar el historial de reportes.\n\nSi la persona ya no trabaja aquí o ya no es cliente, se recomienda simplemente cambiar su estado a Inactivo editando su perfil.\n\n¿Deseas eliminarlo de todos modos?`)) return;

                try {
                    const url = isCliente ? `/api/clientes/${persona.userCode}` : `/api/personal/${persona.userCode}`;
                    const resp = await fetch(url, {
                        method: 'DELETE',
                        headers: this.getHeaders()
                    });

                    if (!resp.ok) throw new Error('No se pudo eliminar');

                    this.globalSuccess = 'Registro eliminado correctamente.';
                    setTimeout(() => this.globalSuccess = '', 3000);
                    await this.cargarDatos();
                } catch (e) {
                    this.globalError = e.message;
                }
            },

            async submitServicioForm() {
                this.modalServicio.loading = true;
                this.modalServicio.error = '';
                try {
                    const isCreate = this.modalServicio.mode === 'create';
                    const url = isCreate ? '/api/servicios' : `/api/servicios/${this.formServicio.serviceCode}`;
                    const method = isCreate ? 'POST' : 'PUT';

                    const resp = await fetch(url, {
                        method: method,
                        headers: this.getHeaders(),
                        body: JSON.stringify(this.formServicio)
                    });

                    if (!resp.ok) {
                        const errData = await resp.json().catch(() => ({}));
                        throw new Error(errData.message || errData.error || 'Ocurrió un error al guardar el servicio');
                    }

                    this.globalSuccess = `Servicio ${isCreate ? 'creado' : 'actualizado'} con éxito.`;
                    setTimeout(() => this.globalSuccess = '', 3000);
                    this.modalServicio.show = false;
                    await this.cargarDatos();
                } catch (e) {
                    this.modalServicio.error = e.message;
                } finally {
                    this.modalServicio.loading = false;
                }
            },

            async desactivarServicio(servicio) {
                if (!confirm(`¿Estás seguro de desactivar el servicio ${servicio.nombre}? No aparecerá al crear nuevas órdenes.`)) return;
                try {
                    const resp = await fetch(`/api/servicios/${servicio.serviceCode}`, {
                        method: 'DELETE',
                        headers: this.getHeaders(),
                        credentials: 'include'
                    });
                    if (!resp.ok) throw new Error('No se pudo desactivar el servicio');

                    this.globalSuccess = 'Servicio desactivado correctamente.';
                    setTimeout(() => this.globalSuccess = '', 3000);
                    await this.cargarDatos();
                } catch (e) {
                    this.globalError = e.message;
                }
            },

            async activarServicio(servicio) {
                try {
                    const resp = await fetch(`/api/servicios/${servicio.serviceCode}/activar`, {
                        method: 'PUT',
                        headers: this.getHeaders(),
                        credentials: 'include'
                    });
                    if (!resp.ok) throw new Error('No se pudo activar el servicio');

                    this.globalSuccess = 'Servicio activado correctamente.';
                    setTimeout(() => this.globalSuccess = '', 3000);
                    await this.cargarDatos();
                } catch (e) {
                    this.globalError = e.message;
                }
            },

            async eliminarServicio(servicio) {
                if (!confirm(`¿Estás seguro de ELIMINAR permanentemente el servicio ${servicio.nombre}? Esta acción no se puede deshacer.`)) return;
                try {
                    const resp = await fetch(`/api/servicios/${servicio.serviceCode}/eliminar`, {
                        method: 'DELETE',
                        headers: this.getHeaders(),
                        credentials: 'include'
                    });
                    if (!resp.ok) throw new Error('No se pudo eliminar el servicio');

                    this.globalSuccess = 'Servicio eliminado permanentemente.';
                    setTimeout(() => this.globalSuccess = '', 3000);
                    await this.cargarDatos();
                } catch (e) {
                    this.globalError = e.message;
                }
            },

            openModalTipoVehiculo() {
                this.formTipoVehiculo = { nombre: '' };
                this.modalTipoVehiculo.error = '';
                this.modalTipoVehiculo.show = true;
            },

            async submitTipoVehiculoForm() {
                this.modalTipoVehiculo.loading = true;
                this.modalTipoVehiculo.error = '';
                try {
                    const resp = await fetch('/api/tipovehiculo', {
                        method: 'POST',
                        headers: this.getHeaders(),
                        credentials: 'include',
                        body: JSON.stringify(this.formTipoVehiculo)
                    });
                    if (!resp.ok) throw new Error('Error al guardar el tipo de vehículo');

                    this.globalSuccess = 'Tipo de vehículo creado con éxito.';
                    setTimeout(() => this.globalSuccess = '', 3000);
                    this.modalTipoVehiculo.show = false;
                    await this.cargarDatos();
                } catch (e) {
                    this.modalTipoVehiculo.error = e.message;
                } finally {
                    this.modalTipoVehiculo.loading = false;
                }
            },

            async desactivarTipoVehiculo(id) {
                if (!confirm('¿Estás seguro de desactivar este tipo de vehículo? No aparecerá en los formularios.')) return;
                try {
                    const resp = await fetch(`/api/tipovehiculo/${id}`, {
                        method: 'DELETE',
                        headers: this.getHeaders(),
                        credentials: 'include'
                    });
                    if (!resp.ok) throw new Error('No se pudo desactivar');
                    this.globalSuccess = 'Tipo de vehículo desactivado.';
                    setTimeout(() => this.globalSuccess = '', 3000);
                    await this.cargarDatos();
                } catch (e) {
                    this.globalError = e.message;
                }
            },

            async activarTipoVehiculo(id) {
                try {
                    const resp = await fetch(`/api/tipovehiculo/${id}/activar`, {
                        method: 'PUT',
                        headers: this.getHeaders(),
                        credentials: 'include'
                    });
                    if (!resp.ok) throw new Error('No se pudo activar');
                    this.globalSuccess = 'Tipo de vehículo activado.';
                    setTimeout(() => this.globalSuccess = '', 3000);
                    await this.cargarDatos();
                } catch (e) {
                    this.globalError = e.message;
                }
            },

            async eliminarTipoVehiculo(id) {
                if (!confirm('¿Estás seguro de ELIMINAR permanentemente este tipo de vehículo? Esto podría afectar órdenes antiguas.')) return;
                try {
                    const resp = await fetch(`/api/tipovehiculo/${id}/eliminar`, {
                        method: 'DELETE',
                        headers: this.getHeaders(),
                        credentials: 'include'
                    });
                    if (!resp.ok) throw new Error('No se pudo eliminar permanentemente');
                    this.globalSuccess = 'Tipo de vehículo eliminado completamente.';
                    setTimeout(() => this.globalSuccess = '', 3000);
                    await this.cargarDatos();
                } catch (e) {
                    this.globalError = e.message;
                }
            },

            async submitServicioForm() {
            this.modalServicio.loading = true;
            this.modalServicio.error = '';
            try {
                const isCreate = this.modalServicio.mode === 'create';
                const url = isCreate ? '/api/servicios' : `/api/servicios/${this.formServicio.serviceCode}`;
                const method = isCreate ? 'POST' : 'PUT';

                const resp = await fetch(url, {
                    method: method,
                    headers: this.getHeaders(),
                    body: JSON.stringify(this.formServicio)
                });

                if (!resp.ok) {
                    const errData = await resp.json().catch(() => ({}));
                    throw new Error(errData.message || errData.error || 'Ocurrió un error al guardar el servicio');
                }

                this.globalSuccess = `Servicio ${isCreate ? 'creado' : 'actualizado'} con éxito.`;
                setTimeout(() => this.globalSuccess = '', 3000);
                this.modalServicio.show = false;
                await this.cargarDatos();
            } catch (e) {
                this.modalServicio.error = e.message;
            } finally {
                this.modalServicio.loading = false;
            }
        },

                async desactivarServicio(servicio) {
            if (!confirm(`¿Estás seguro de desactivar el servicio ${servicio.nombre}? No aparecerá al crear nuevas órdenes.`)) return;
            try {
                const resp = await fetch(`/api/servicios/${servicio.serviceCode}`, {
                    method: 'DELETE',
                    headers: this.getHeaders(),
                    credentials: 'include'
                });
                if (!resp.ok) throw new Error('No se pudo desactivar el servicio');

                this.globalSuccess = 'Servicio desactivado correctamente.';
                setTimeout(() => this.globalSuccess = '', 3000);
                await this.cargarDatos();
            } catch (e) {
                this.globalError = e.message;
            }
        },

                async activarServicio(servicio) {
            try {
                const resp = await fetch(`/api/servicios/${servicio.serviceCode}/activar`, {
                    method: 'PUT',
                    headers: this.getHeaders(),
                    credentials: 'include'
                });
                if (!resp.ok) throw new Error('No se pudo activar el servicio');

                this.globalSuccess = 'Servicio activado correctamente.';
                setTimeout(() => this.globalSuccess = '', 3000);
                await this.cargarDatos();
            } catch (e) {
                this.globalError = e.message;
            }
        },

                async eliminarServicio(servicio) {
            if (!confirm(`¿Estás seguro de ELIMINAR permanentemente el servicio ${servicio.nombre}? Esta acción no se puede deshacer.`)) return;
            try {
                const resp = await fetch(`/api/servicios/${servicio.serviceCode}/eliminar`, {
                    method: 'DELETE',
                    headers: this.getHeaders(),
                    credentials: 'include'
                });
                if (!resp.ok) throw new Error('No se pudo eliminar el servicio');

                this.globalSuccess = 'Servicio eliminado permanentemente.';
                setTimeout(() => this.globalSuccess = '', 3000);
                await this.cargarDatos();
            } catch (e) {
                this.globalError = e.message;
            }
        },

        openModalTipoVehiculo() {
            this.formTipoVehiculo = { nombre: '' };
            this.modalTipoVehiculo.error = '';
            this.modalTipoVehiculo.show = true;
        },

                async submitTipoVehiculoForm() {
            this.modalTipoVehiculo.loading = true;
            this.modalTipoVehiculo.error = '';
            try {
                const resp = await fetch('/api/tipovehiculo', {
                    method: 'POST',
                    headers: this.getHeaders(),
                    credentials: 'include',
                    body: JSON.stringify(this.formTipoVehiculo)
                });
                if (!resp.ok) throw new Error('Error al guardar el tipo de vehículo');

                this.globalSuccess = 'Tipo de vehículo creado con éxito.';
                setTimeout(() => this.globalSuccess = '', 3000);
                this.modalTipoVehiculo.show = false;
                await this.cargarDatos();
            } catch (e) {
                this.modalTipoVehiculo.error = e.message;
            } finally {
                this.modalTipoVehiculo.loading = false;
            }
        },

                async desactivarTipoVehiculo(id) {
            if (!confirm('¿Estás seguro de desactivar este tipo de vehículo? No aparecerá en los formularios.')) return;
            try {
                const resp = await fetch(`/api/tipovehiculo/${id}`, {
                    method: 'DELETE',
                    headers: this.getHeaders(),
                    credentials: 'include'
                });
                if (!resp.ok) throw new Error('No se pudo desactivar');
                this.globalSuccess = 'Tipo de vehículo desactivado.';
                setTimeout(() => this.globalSuccess = '', 3000);
                await this.cargarDatos();
            } catch (e) {
                this.globalError = e.message;
            }
        },

                async activarTipoVehiculo(id) {
            try {
                const resp = await fetch(`/api/tipovehiculo/${id}/activar`, {
                    method: 'PUT',
                    headers: this.getHeaders(),
                    credentials: 'include'
                });
                if (!resp.ok) throw new Error('No se pudo activar');
                this.globalSuccess = 'Tipo de vehículo activado.';
                setTimeout(() => this.globalSuccess = '', 3000);
                await this.cargarDatos();
            } catch (e) {
                this.globalError = e.message;
            }
        },

                async eliminarTipoVehiculo(id) {
            if (!confirm('¿Estás seguro de ELIMINAR permanentemente este tipo de vehículo? Esto podría afectar órdenes antiguas.')) return;
            try {
                const resp = await fetch(`/api/tipovehiculo/${id}/eliminar`, {
                    method: 'DELETE',
                    headers: this.getHeaders(),
                    credentials: 'include'
                });
if (!resp.ok) throw new Error('No se pudo eliminar permanentemente');
                this.globalSuccess = 'Tipo de vehículo eliminado completamente.';
                setTimeout(() => this.globalSuccess = '', 3000);
                await this.cargarDatos();
            } catch (e) {
                this.globalError = e.message;
            }
        },

        async logout() {
            try {
                await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
            } catch (e) { }
            sessionStorage.removeItem('rol');
            window.location.href = '/';
        },

        async buscarVehiculoEspecifico() {
            this.vehiculoBuscado = null;
            this.clienteEncontradoVehiculos = null;
            this.vehiculosDelCliente = [];
            this.historialOrdenesVehiculo = [];
            this.searchIntento = true;

            if (!this.searchVehiculoDoc && !this.searchVehiculoPlaca) {
                return alert('Debe ingresar el documento del cliente o la placa del vehículo.');
            }

            try {
                if (this.searchVehiculoPlaca) {
                    const res = await fetch(`/api/vehiculos/${this.searchVehiculoPlaca}`, { headers: this.getHeaders(), credentials: 'include' });
                    if (res.ok) {
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
                    if (!this.clienteEncontradoVehiculos) return alert('Cliente no encontrado en el sistema.');

                    const res = await fetch(`/api/vehiculos/cliente/${this.clienteEncontradoVehiculos.userCode}`, { headers: this.getHeaders(), credentials: 'include' });
                    if (res.ok) {
                        this.vehiculosDelCliente = await res.json();
                        if (this.vehiculosDelCliente.length === 0) {
                            alert('El cliente no tiene vehículos registrados.');
                        }
                    }
                }
            } catch (e) { }
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

        openModalVehiculoGlobal(mode, veh) {
            this.modalVehiculoGlobal.mode = mode;
            this.formVehiculoGlobal = { ...veh, tipoVehiculo: { id: veh.tipoVehiculo?.id } };
            this.modalVehiculoGlobal.show = true;
        },

        async submitVehiculoGlobal() {
            this.modalVehiculoGlobal.loading = true;
            try {
                const url = `/api/vehiculos/${this.formVehiculoGlobal.placa}`;
                const res = await fetch(url, { method: 'PUT', headers: this.getHeaders(), credentials: 'include', body: JSON.stringify(this.formVehiculoGlobal) });
                if (!res.ok) throw new Error('Error al guardar');
                this.modalVehiculoGlobal.show = false;
                this.buscarVehiculoEspecifico();
            } catch (e) { this.modalVehiculoGlobal.error = e.message; }
            finally { this.modalVehiculoGlobal.loading = false; }
        },
        parseDate(dateInput) {
            if (!dateInput) return null;
            if (Array.isArray(dateInput)) {
                return new Date(Date.UTC(dateInput[0], dateInput[1] - 1, dateInput[2], dateInput[3] || 0, dateInput[4] || 0, dateInput[5] || 0));
            }
            if (typeof dateInput === 'string' && !dateInput.endsWith('Z') && dateInput.includes('T')) {
                return new Date(dateInput + 'Z');
            }
            return new Date(dateInput);
        },

        formatDate(dateInput) {
            const d = this.parseDate(dateInput);
            return d ? d.toLocaleDateString() : '';
        },
                
        async eliminarVehiculoGlobal(placa) {
            if (!confirm('¿Eliminar vehículo?')) return;
            await fetch(`/api/vehiculos/${placa}`, { method: 'DELETE', headers: this.getHeaders(), credentials: 'include' });
            this.buscarVehiculoEspecifico();
        }
    }
}
window.adminPage = adminPage;

