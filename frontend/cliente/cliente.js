        function clienteDashboard() {
            return {
                view: 'stats', 
                loading: false,
                updateMsg: '',
                updateSuccess: false,
                userData: {
                    nombre: 'Cargando...',
                    apellido: '',
                    telefono: '',
                    direccion: '',
                    correo: '',
                    userCode: '',
                    documento: ''
                },
                statsHoy: {
                    lavados: 0,
                    canceladas: 0,
                    puntos: 0
                },
                statsHistorial: {
                    totalLavados: 0,
                    puntos: 0,
                    totalCanceladas: 0,
                    ultimaVisita: 'Sin registros',
                    totalGastado: 0,
                    servicioFavorito: '-'
                },
                vehiculos: [],
                ordenesHoy: [],
                ordenesHistorial: [],
                tiposVehiculo: [],
                formVehiculo: { placa: '', marca: '', modelo: '', color: '', year: '', tipoVehiculo: { id: '' } },
                vehiculoLoading: false,
                vehiculoMsg: '',
                vehiculoMode: 'create',
                searchVehiculo: '',
                
                modalOrden: {
                    show: false,
                    orden: null,
                    empleadoNombre: ''
                },

                vehiculosFiltrados() {
                    if (!this.searchVehiculo) return this.vehiculos;
                    const q = this.searchVehiculo.toLowerCase();
                    return this.vehiculos.filter(v => 
                        v.placa.toLowerCase().includes(q) || 
                        v.marca.toLowerCase().includes(q) ||
                        v.modelo.toLowerCase().includes(q)
                    );
                },

                editarVehiculo(veh) {
                    this.vehiculoMode = 'edit';
                    this.formVehiculo = {
                        placa: veh.placa,
                        marca: veh.marca,
                        modelo: veh.modelo,
                        color: veh.color,
                        year: veh.year,
                        tipoVehiculo: { id: veh.tipoVehiculo ? veh.tipoVehiculo.id : '' }
                    };
                    window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
                },

                async eliminarVehiculo(placa) {
                    if(!confirm('¿Seguro que deseas eliminar (ocultar) este vehículo?')) return;
                    try {
                        const res = await fetch(`/api/vehiculos/${placa}`, { method: 'DELETE', headers: this.getHeaders(), credentials: 'include' });
                        if(res.ok) {
                            this.cargarVehiculos();
                        } else {
                            alert('Error al eliminar vehículo');
                        }
                    } catch(e) { console.error(e); }
                },
                vehiculoSuccess: false,

                init() {
                    this.checkAuth();
                    this.fetchUserData();
                },

                checkAuth() {
                    const rol = sessionStorage.getItem('rol');
                    if (!rol || rol !== 'CLIENTE') {
                        window.location.href = '/index.html';
                    }
                },

                async logout() {
                    try {
                        await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
                    } catch (e) {}
                    sessionStorage.removeItem('rol');
                    window.location.href = '/index.html';
                },

                getHeaders() {
                    return {
                        'Content-Type': 'application/json'
                    };
                },

                async fetchUserData() {
                    try {
                        const meRes = await fetch('/api/auth/me', { credentials: 'include' });
                        if(!meRes.ok) { this.logout(); return; }
                        const meData = await meRes.json();
                        const userCode = meData.userCode;
                        
                        const res = await fetch(`/api/clientes/${userCode}`, { headers: this.getHeaders(), credentials: 'include' });
                        if(res.ok) {
                            this.userData = await res.json();
                            this.cargarVehiculos();
                            this.cargarTiposVehiculo();
                            this.cargarOrdenes();
                        } else {
                            this.logout();
                        }
                    } catch(e) {
                        console.error('Error fetching user data', e);
                        this.logout();
                    }
                },

                async cargarVehiculos() {
                    try {
                        const res = await fetch(`/api/vehiculos/cliente/${this.userData.userCode}`, { headers: this.getHeaders(), credentials: 'include' });
                        if(res.ok) {
                            this.vehiculos = await res.json();
                        }
                    } catch(e) { console.error('Error fetching vehicles', e); }
                },

                async cargarTiposVehiculo() {
                    try {
                        const res = await fetch(`/api/tipovehiculo`, { headers: this.getHeaders(), credentials: 'include' });
                        if(res.ok) {
                            this.tiposVehiculo = await res.json();
                        }
                    } catch(e) { console.error('Error fetching vehicle types', e); }
                },

                parseDate(dateInput) {
                    if (!dateInput) return null;
                    if (Array.isArray(dateInput)) {
                        return new Date(dateInput[0], dateInput[1] - 1, dateInput[2], dateInput[3] || 0, dateInput[4] || 0, dateInput[5] || 0);
                    }
                    return new Date(dateInput);
                },

                formatDate(dateInput) {
                    const d = this.parseDate(dateInput);
                    return d ? d.toLocaleDateString() : 'N/A';
                },

                formatDateTime(dateInput) {
                    const d = this.parseDate(dateInput);
                    return d ? d.toLocaleString() : 'N/A';
                },

                async verDetallesOrden(orden) {
                    this.modalOrden.orden = orden;
                    this.modalOrden.empleadoNombre = 'Cargando...';
                    this.modalOrden.show = true;
                    
                    try {
                        const res = await fetch(`/api/personal/${orden.personalId}`, { headers: this.getHeaders(), credentials: 'include' });
                        if(res.ok) {
                            const emp = await res.json();
                            this.modalOrden.empleadoNombre = emp.nombre + ' ' + emp.apellido;
                        } else {
                            this.modalOrden.empleadoNombre = orden.personalId;
                        }
                    } catch(e) {
                        this.modalOrden.empleadoNombre = orden.personalId;
                    }
                },

                async cargarOrdenes() {
                    try {
                        const res = await fetch(`/api/ordenes/cliente/${this.userData.userCode}`, { headers: this.getHeaders(), credentials: 'include' });
                        if(res.ok) {
                            const data = await res.json();
                            const ordenesList = Array.isArray(data) ? data : (data.content || []);
                            const hoy = new Date().toDateString();
                            
                            this.statsHistorial.totalCanceladas = ordenesList.filter(o => o.estado === 'CANCELADO').length;
                            this.statsHoy.canceladas = ordenesList.filter(o => o.estado === 'CANCELADO' && o.fechaFin && this.parseDate(o.fechaFin).toDateString() === hoy).length;

                            
                            this.ordenesHistorial = ordenesList.filter(o => o.estado === 'FINALIZADO' || o.estado === 'CANCELADO').sort((a,b) => {
                                const d1 = this.parseDate(a.fechaInicio);
                                const d2 = this.parseDate(b.fechaInicio);
                                return (d2 ? d2.getTime() : 0) - (d1 ? d1.getTime() : 0);
                            });
                            
                            this.ordenesHoy = this.ordenesHistorial.filter(o => o.fechaFin && this.parseDate(o.fechaFin).toDateString() === hoy);
                            
                            const finalizadasHistorial = this.ordenesHistorial.filter(o => o.estado === 'FINALIZADO');
                            const finalizadasHoy = this.ordenesHoy.filter(o => o.estado === 'FINALIZADO');

                            this.statsHistorial.totalLavados = finalizadasHistorial.length;
                            this.statsHistorial.puntos = finalizadasHistorial.length * 10;
                            this.statsHistorial.totalGastado = finalizadasHistorial.reduce((acc, o) => acc + o.total, 0);
                            
                            const srvCounts = {};
                            finalizadasHistorial.forEach(o => {
                                if(o.servicio) o.servicio.forEach(s => {
                                    srvCounts[s.nombre] = (srvCounts[s.nombre] || 0) + 1;
                                });
                            });
                            const fav = Object.entries(srvCounts).sort((a,b) => b[1]-a[1])[0];
                            this.statsHistorial.servicioFavorito = fav ? fav[0] : 'Ninguno';
                            
                            this.statsHoy.lavados = finalizadasHoy.length;
                            this.statsHoy.puntos = finalizadasHoy.length * 10;
                            
                            if (this.ordenesHistorial.length > 0) {
                                this.statsHistorial.ultimaVisita = this.formatDate(this.ordenesHistorial[0].fechaFin || this.ordenesHistorial[0].fechaInicio);
                            } else {
                                this.statsHistorial.ultimaVisita = 'Sin registros';
                            }
                        }
                    } catch(e) { console.error('Error fetching orders', e); }
                },

                async updateProfile() {
                    this.loading = true;
                    this.updateMsg = '';
                    
                    try {
                        const res = await fetch(`/api/clientes/${this.userData.userCode}`, {
                            method: 'PUT',
                            headers: this.getHeaders(),
                            credentials: 'include',
                            body: JSON.stringify({
                                nombre: this.userData.nombre,
                                apellido: this.userData.apellido,
                                telefono: this.userData.telefono,
                                direccion: this.userData.direccion,
                                correo: this.userData.correo,
                                documento: this.userData.documento
                            })
                        });

                        if(!res.ok) throw new Error('No se pudo actualizar el perfil');

                        this.updateSuccess = true;
                        this.updateMsg = 'Datos actualizados correctamente.';
                        setTimeout(() => this.updateMsg = '', 3000);
                    } catch(e) {
                        this.updateSuccess = false;
                        this.updateMsg = e.message;
                    } finally {
                        this.loading = false;
                    }
                },

                async registrarVehiculo() {
                    this.vehiculoLoading = true;
                    this.vehiculoMsg = '';
                    
                    try {
                        const payload = { ...this.formVehiculo, documentoCliente: this.userData.documento, activo: true };
                        
                        const method = this.vehiculoMode === 'create' ? 'POST' : 'PUT';
                        const url = this.vehiculoMode === 'create' ? '/api/vehiculos' : `/api/vehiculos/${this.formVehiculo.placa}`;

                        const res = await fetch(url, {
                            method: method,
                            headers: this.getHeaders(),
                            credentials: 'include',
                            body: JSON.stringify(payload)
                        });

                        if(!res.ok) {
                            const err = await res.json().catch(()=>({}));
                            throw new Error(err.error || err.message || 'Error al guardar vehículo');
                        }

                        this.vehiculoSuccess = true;
                        this.vehiculoMsg = `Vehículo ${this.vehiculoMode === 'create' ? 'registrado' : 'actualizado'} correctamente.`;
                        this.formVehiculo = { placa: '', marca: '', modelo: '', color: '', year: '', tipoVehiculo: { id: '' } };
                        this.vehiculoMode = 'create';
                        this.cargarVehiculos();
                        
                        setTimeout(() => this.vehiculoMsg = '', 3000);
                    } catch(e) {
                        this.vehiculoSuccess = false;
                        this.vehiculoMsg = e.message;
                    } finally {
                        this.vehiculoLoading = false;
                    }
                }
            }
        }
window.clienteDashboard = clienteDashboard;

