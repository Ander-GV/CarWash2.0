        function empleadoPage() {
            return {
                view: 'tareas',
                currentUser: '',
                userCode: '',
                tareas: [],
                todasOrdenes: [],
                paginaTareas: 1,
                elementosPorPagina: 6,
                loading: false,
                globalError: '',
                globalSuccess: '',
                filtroFechaHistorial: '',
                
                userData: {
                    nombre: '', apellido: '', telefono: '', direccion: '', correo: '', activo: true, disponibleHoy: true
                },
                loadingProfile: false,
                updateMsg: '',
                updateSuccess: false,
                
                modalProblema: { show: false, tarea: null, descripcion: '', loading: false, error: '' },

                async init() {
                    const rol = sessionStorage.getItem('rol');
                    if (!rol || rol !== 'EMPLEADO') { window.location.href = '/'; return; }
                    this.currentUser = 'Empleado';
                    
                    try {
                        const meRes = await fetch('/api/auth/me', { credentials: 'include' });
                        if(!meRes.ok) { this.logout(); return; }
                        const meData = await meRes.json();
                        this.userCode = meData.userCode;
                        
                        const profileRes = await fetch(`/api/personal/${this.userCode}`, { headers: this.getHeaders(), credentials: 'include' });
                        if(profileRes.ok) {
                            const pData = await profileRes.json();
                            this.userData = pData;
                            this.currentUser = pData.nombre + ' ' + pData.apellido;
                        }
                    } catch(e) {
                        this.logout();
                        return;
                    }

                    if (!sessionStorage.getItem('empleadoSessionStart')) {
                        sessionStorage.setItem('empleadoSessionStart', new Date().toISOString());
                    }

                    this.cargarMisTareas();
                },

                getHeaders() {
                    return {
                        'Content-Type': 'application/json'
                    };
                },

                async cargarMisTareas() {
                    this.loading = true;
                    this.globalError = '';
                    try {
                        const res = await fetch('/api/ordenes?size=1000&sort=fechaInicio,desc', { headers: this.getHeaders(), credentials: 'include' });
                        if(res.ok) {
                            const data = await res.json();
                            const todas = Array.isArray(data) ? data : (data.content || []);
                            this.todasOrdenes = todas.filter(o => o.personalId === this.userCode);
                            
                            this.tareas = this.todasOrdenes
                                .filter(o => o.estado !== 'CONFIRMADO' && o.estado !== 'FINALIZADO' && o.estado !== 'CON_PROBLEMAS' && o.estado !== 'CANCELADO')
                                .sort((a,b) => {
                                    const d1 = this.parseDate(a.fechaInicio);
                                    const d2 = this.parseDate(b.fechaInicio);
                                    return (d1 ? d1.getTime() : 0) - (d2 ? d2.getTime() : 0);
                                });
                        } else {
                            throw new Error('No se pudieron cargar las tareas.');
                        }
                    } catch(e) {
                        this.globalError = e.message;
                    } finally {
                        this.loading = false;
                    }
                },

                parseDate(dateInput) {
                    if (!dateInput) return null;
                    if (Array.isArray(dateInput)) {
                        return new Date(dateInput[0], dateInput[1] - 1, dateInput[2], dateInput[3] || 0, dateInput[4] || 0, dateInput[5] || 0);
                    }
                    if (typeof dateInput === 'string' && !dateInput.endsWith('Z') && dateInput.includes('T')) {
                        
                    }
                    return new Date(dateInput);
                },

                formatDate(dateInput) {
                    const d = this.parseDate(dateInput);
                    return d ? d.toLocaleDateString() + ' ' + d.toLocaleTimeString() : 'N/A';
                },

                formatTime(dateInput) {
                    const d = this.parseDate(dateInput);
                    return d ? d.toLocaleTimeString() : 'N/A';
                },

                tareasPaginadas() {
                    const start = (this.paginaTareas - 1) * this.elementosPorPagina;
                    if (start >= this.tareas.length && this.tareas.length > 0) {
                        this.paginaTareas = Math.ceil(this.tareas.length / this.elementosPorPagina);
                        return this.tareas.slice((this.paginaTareas - 1) * this.elementosPorPagina, this.paginaTareas * this.elementosPorPagina);
                    }
                    return this.tareas.slice(start, start + this.elementosPorPagina);
                },
                
                cambiarPaginaTareas(delta) {
                    this.paginaTareas += delta;
                },
                
                hayPaginaAnteriorTareas() {
                    return this.paginaTareas > 1;
                },
                
                hayPaginaSiguienteTareas() {
                    return this.tareas.length > this.paginaTareas * this.elementosPorPagina;
                },

                ordenesEnCola() { return this.todasOrdenes.filter(o => o.estado === 'PENDIENTE'); },
                ordenesEnProceso() { return this.todasOrdenes.filter(o => o.estado === 'EN_PROCESO'); },
                ordenesTerminadas() { 
                    const hoy = new Date().toDateString();
                    return this.todasOrdenes.filter(o => {
                        if (o.estado === 'ESPERANDO_CONFIRMACION') return true;
                        if (o.estado === 'FINALIZADO' || o.estado === 'CONFIRMADO') {
                            if (!o.fechaFin) return false;
                            return this.parseDate(o.fechaFin).toDateString() === hoy;
                        }
                        return false;
                    });
                },

                historialOrdenes() {
                    const filtradas = this.lavadosHoy();
                    return filtradas.sort((a,b) => {
                        const d1 = this.parseDate(a.fechaFin);
                        const d2 = this.parseDate(b.fechaFin);
                        return (d2 ? d2.getTime() : 0) - (d1 ? d1.getTime() : 0);
                    });
                },

                lavadosHoy() {
                    const sessionStartStr = sessionStorage.getItem('empleadoSessionStart');
                    if (!sessionStartStr) return [];
                    const sessionStart = new Date(sessionStartStr);
                    
                    return this.todasOrdenes.filter(o => {
                        if (o.estado !== 'FINALIZADO' && o.estado !== 'CONFIRMADO') return false;
                        if (!o.fechaFin) return false;
                        const d = this.parseDate(o.fechaFin);
                        return d && d >= sessionStart;
                    });
                },

                incidenciasHoy() {
                    const sessionStartStr = sessionStorage.getItem('empleadoSessionStart');
                    if (!sessionStartStr) return [];
                    const sessionStart = new Date(sessionStartStr);
                    
                    return this.todasOrdenes.filter(o => {
                        if (o.estado !== 'CON_PROBLEMAS') return false;
                        if (!o.fechaFin) return false;
                        const d = this.parseDate(o.fechaFin);
                        return d && d >= sessionStart;
                    }).sort((a,b) => {
                        const d1 = this.parseDate(a.fechaFin);
                        const d2 = this.parseDate(b.fechaFin);
                        return (d2 ? d2.getTime() : 0) - (d1 ? d1.getTime() : 0);
                    });
                },

                totalFacturadoHoy() {
                    return this.lavadosHoy().reduce((acc, o) => acc + o.total, 0);
                },

                async updateProfile() {
                    this.loadingProfile = true;
                    this.updateMsg = '';
                    
                    try {
                        const payload = {
                            nombre: this.userData.nombre,
                            apellido: this.userData.apellido,
                            telefono: this.userData.telefono,
                            correo: this.userData.correo,
                            direccion: this.userData.direccion,
                            activo: this.userData.activo,
                            disponibleHoy: this.userData.disponibleHoy,
                            rol: (this.userData.roles && this.userData.roles.length > 0) ? this.userData.roles[0] : 'EMPLEADO',
                            password: '' 
                        };

                        const res = await fetch(`/api/personal/${this.userCode}`, {
                            method: 'PUT',
                            headers: this.getHeaders(),
                            credentials: 'include',
                            body: JSON.stringify(payload)
                        });

                        if(!res.ok) {
                            const errData = await res.text();
                            throw new Error('No se pudo actualizar el perfil: ' + errData);
                        }

                        this.updateSuccess = true;
                        this.updateMsg = 'Datos actualizados correctamente.';
                        setTimeout(() => this.updateMsg = '', 3000);
                    } catch(e) {
                        this.updateSuccess = false;
                        this.updateMsg = e.message;
                    } finally {
                        this.loadingProfile = false;
                    }
                },

                async cambiarEstado(id, nuevoEstado) {
                    console.log("Intentando cambiar estado de:", id, " a ", nuevoEstado);
                    this.globalError = '';
                    try {
                        const res = await fetch(`/api/ordenes/${id}/estado`, {
                            method: 'PATCH',
                            headers: this.getHeaders(),
                            credentials: 'include',
                            body: JSON.stringify({ estado: nuevoEstado })
                        });
                        if(!res.ok) {
                            const errorText = await res.text();
                            throw new Error(`Status ${res.status}: ${errorText}`);
                        }
                        
                        this.globalSuccess = `Orden marcada como ${nuevoEstado.replace('_', ' ')}.`;
                        setTimeout(() => this.globalSuccess = '', 3000);
                        
                        this.cargarMisTareas();
                    } catch(e) {
                        this.globalError = e.message;
                    }
                },
                
                abrirModalProblema(tarea) {
                    this.modalProblema.tarea = tarea;
                    this.modalProblema.descripcion = '';
                    this.modalProblema.error = '';
                    this.modalProblema.show = true;
                },
                
                async submitProblema() {
                    this.modalProblema.loading = true;
                    this.modalProblema.error = '';
                    try {
                        const res = await fetch(`/api/ordenes/${this.modalProblema.tarea.ordenCode}/estado`, {
                            method: 'PATCH',
                            headers: this.getHeaders(),
                            credentials: 'include',
                            body: JSON.stringify({ 
                                estado: 'CON_PROBLEMAS',
                                descripcionProblema: this.modalProblema.descripcion
                            })
                        });
                        if(!res.ok) {
                            let errorMsg = 'Error al reportar el problema';
                            try { const errData = await res.json(); errorMsg = errData.message || errData.error || errorMsg; } catch(e) { errorMsg = await res.text(); }
                            throw new Error(errorMsg);
                        }
                        
                        this.globalSuccess = 'Problema reportado exitosamente al encargado.';
                        setTimeout(() => this.globalSuccess = '', 3000);
                        this.modalProblema.show = false;
                        
                        
                        if (this.tareas) {
                            this.tareas = this.tareas.filter(t => t.ordenCode !== this.modalProblema.tarea.ordenCode);
                        }
                        
                        this.cargarMisTareas();
                    } catch(e) {
                        this.modalProblema.error = e.message;
                    } finally {
                        this.modalProblema.loading = false;
                    }
                },

                async logout() {
                    try {
                        await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
                    } catch (e) {}
                    sessionStorage.removeItem('rol');
                    sessionStorage.removeItem('empleadoSessionStart');
                    window.location.href = '/';
                }
            }
        }

