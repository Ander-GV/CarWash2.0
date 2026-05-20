# Solución Exacta para tu Topología de Packet Tracer

Esta solución se adapta **exactamente** al diseño físico de tu captura de pantalla, conectando:
* **Router R1** (`G0/0/0`) -> **SW1** (`Fa0/24`)
* **Router R1** (`G0/1`) -> **SW2** (`Fa0/24`)
* **Sin conexión directa entre SW1 y SW2**.

---

## 1. Configuración de Direccionamiento IP para las PCs

Para que esta topología funcione sin enlaces troncales entre switches, cada rama del router debe tener su propio rango IP. Configura las PCs en Packet Tracer (**Desktop > IP Configuration**) con los siguientes valores:

### PCs conectadas al Switch 1 (SW1)
* **PCu (VLAN 10 - Ventas)**:
  * IP: `192.168.1.10`
  * Máscara: `255.255.255.192`
  * Gateway: *(Vacío)*
* **PC1 (VLAN 20 - Compras)**:
  * IP: `192.168.1.130`
  * Máscara: `255.255.255.240`
  * Gateway: `192.168.1.129`
* **PC2 (VLAN 30 - Admon)**:
  * IP: `192.168.1.162`
  * Máscara: `255.255.255.224`
  * Gateway: `192.168.1.161`

### PCs conectadas al Switch 2 (SW2)
* **PC3 (VLAN 10 - Ventas)**:
  * IP: `192.168.1.70`
  * Máscara: `255.255.255.192`
  * Gateway: *(Vacío)*
* **PC4 (VLAN 20 - Compras)**:
  * IP: `192.168.1.146`
  * Máscara: `255.255.255.240`
  * Gateway: `192.168.1.145`
* **PC5 / PC-PT (VLAN 30 - Admon)**:
  * IP: `192.168.1.194`
  * Máscara: `255.255.255.224`
  * Gateway: `192.168.1.193`

---

## 2. Comandos CLI para los Switches (Copiar y Pegar)

### Switch 1 (SW1)
```ios
enable
configure terminal
hostname SW1

! 1. Crear VLANs
vlan 10
 name Ventas
vlan 20
 name Compras
vlan 30
 name Admon
exit

! 2. Configurar puertos de acceso para las PCs
interface fa0/1
 switchport mode access
 switchport access vlan 10
 no shutdown
exit

interface fa0/2
 switchport mode access
 switchport access vlan 20
 no shutdown
exit

interface fa0/3
 switchport mode access
 switchport access vlan 30
 no shutdown
exit

! 3. Configurar puerto troncal hacia el Router (G0/0/0)
interface fa0/24
 switchport mode trunk
 description Enlace_a_Router_R1
 no shutdown
exit

write memory
```

### Switch 2 (SW2)
```ios
enable
configure terminal
hostname SW2

! 1. Crear VLANs
vlan 10
 name Ventas
vlan 20
 name Compras
vlan 30
 name Admon
exit

! 2. Configurar puertos de acceso para las PCs
interface fa0/1
 switchport mode access
 switchport access vlan 10
 no shutdown
exit

interface fa0/2
 switchport mode access
 switchport access vlan 20
 no shutdown
exit

interface fa0/3
 switchport mode access
 switchport access vlan 30
 no shutdown
exit

! 3. Configurar puerto troncal hacia el Router (G0/1)
interface fa0/24
 switchport mode trunk
 description Enlace_a_Router_R1
 no shutdown
exit

write memory
```

---

## 3. Comandos CLI para el Router R1 (Cisco 2911)

Aplica estos comandos en la consola del router para habilitar las interfaces y configurar el enrutamiento Inter-VLAN para ambos lados:

```ios
enable
configure terminal
hostname R1

! ==========================================
! CONFIGURACIÓN HACIA SWITCH 1 (Puerto G0/0/0)
! ==========================================

! Activar puerto físico
interface g0/0/0
 no ip address
 no shutdown
exit

! Subinterfaz para VLAN 20 (Compras en SW1)
interface g0/0/0.20
 encapsulation dot1Q 20
 ip address 192.168.1.129 255.255.255.240
 no shutdown
exit

! Subinterfaz para VLAN 30 (Admon en SW1)
interface g0/0/0.30
 encapsulation dot1Q 30
 ip address 192.168.1.161 255.255.255.224
 no shutdown
exit


! ==========================================
! CONFIGURACIÓN HACIA SWITCH 2 (Puerto G0/1)
! ==========================================

! Activar puerto físico
interface g0/1
 no ip address
 no shutdown
exit

! Subinterfaz para VLAN 20 (Compras en SW2)
interface g0/1.20
 encapsulation dot1Q 20
 ip address 192.168.1.145 255.255.255.240
 no shutdown
exit

! Subinterfaz para VLAN 30 (Admon en SW2)
interface g0/1.30
 encapsulation dot1Q 30
 ip address 192.168.1.193 255.255.255.224
 no shutdown
exit

write memory
```

---

## 4. Pruebas de Conectividad (Verificación)

1. **PC1 (SW1)** -> ping `192.168.1.146` (PC4 en SW2): **EXITOSO**. (El router enruta entre interfaces `g0/0/0.20` y `g0/1.20`).
2. **PC2 (SW1)** -> ping `192.168.1.194` (PC5 en SW2): **EXITOSO**.
3. **PC1 (SW1)** -> ping `192.168.1.10` (PCu en SW1): **FALLIDO** (La VLAN 10 está correctamente aislada y no tiene enrutamiento).
