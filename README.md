# Dental Clinic Management System 🦷

![Java](https://img.shields.io/badge/java-21-%23ED8B00.svg?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.11-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=Spring-Security&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=flat&logo=Thymeleaf&logoColor=white)
![MapStruct](https://img.shields.io/badge/MapStruct-gray?style=flat)
![H2](https://img.shields.io/badge/H2_Database-blue?style=flat)
![Bootstrap](https://img.shields.io/badge/Bootstrap-4-%238511FA.svg?style=flat&logo=bootstrap&logoColor=white)

Este proyecto es un sistema para uso interno de una clínica dental, diseñado para optimizar y automatizar la administración diaria del consultorio. Facilita la interacción entre el personal administrativo, los profesionales de la salud y la gestión integral de la clínica a través de un panel de control basado en roles.

---

## 🚀 Estado Actual del Proyecto

Actualmente, el sistema se encuentra en **desarrollo activo**. El **Módulo de Administración Core ha sido completado al 100%**, incluyendo interfaces robustas, seguridad, validaciones y pruebas de integración ("End-to-End").

### ✅ Características Implementadas (Rol Admin)
- **Seguridad y Autenticación:** Control de acceso basado en roles (`ADMIN`, `DENTIST`, `RECEPTIONIST`) mediante Spring Security.
- **Gestión de Usuarios (Odontólogos y Recepcionistas):**
  - Operaciones CRUD completas con validación de datos en backend y frontend.
  - Activación y Desactivación lógica (*Soft Delete*).
  - Listados dinámicos con paginación avanzada y filtros de búsqueda (implementados mediante Spring Data JPA Specifications).
- **Gestión Clínica:**
  - **Catálogo de Servicios:** Administración de los tratamientos ofrecidos por la clínica (costos, duración, etc.).
  - **Asignación de Horarios (Schedules):** Panel intuitivo para asignar y gestionar los horarios semanales de trabajo de los odontólogos.
- **Calidad de Software (QA):**
  - **Pruebas de Integración:** Suite de pruebas con `@SpringBootTest` y `MockMvc` configuradas con un entorno de base de datos H2 aislado.
  - **Slice Tests:** Pruebas enfocadas en las capas de Controladores Web.

---

## 🗺️ Roadmap (Pendientes por Desarrollar)

Aún quedan módulos vitales para la finalización del sistema:

- [ ] **Módulo Recepcionista:**
  - Panel de control propio (Dashboard).
  - Gestión integral de Pacientes (CRUD, Historial clínico, Asignación de "Responsables" para menores).
  - Programación de Citas (Agendar, reprogramar o cancelar).
- [ ] **Módulo Odontólogo:**
  - Panel de control propio (Dashboard).
  - Visualización de la agenda diaria de citas.
  - Consulta y actualización de expedientes de pacientes.
- [ ] **Notificaciones:** Implementación de envío de correos automatizados (Thymeleaf templates).
- [ ] **Base de Datos de Producción:** Migración de H2 a una base de datos relacional robusta (ej. PostgreSQL o MySQL) para entornos de producción.

---

## 🏛️ Arquitectura y Mejores Prácticas

El proyecto ha sido desarrollado siguiendo estándares profesionales y metodologías sólidas:
- **Separación de Responsabilidades:** Arquitectura clásica basada en Controladores, Servicios y Repositorios.
- **Transferencia de Datos:** Uso estricto de DTOs (`Records` de Java) para proteger las entidades del dominio.
- **Mapeo Automatizado:** Utilización de `MapStruct` para conversiones rápidas y seguras entre Entidades y DTOs.
- **Consultas Dinámicas:** Uso de `JpaSpecificationExecutor` para búsquedas multi-paramétricas sin escribir grandes consultas SQL (evitando "God Classes").
- **Vistas Modulares:** Plantillas en Thymeleaf fragmentadas para reutilización de componentes HTML (layouts, modals, navbars).

---

## 🛠️ Cómo ejecutar el proyecto en entorno local

1. **Clona este repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/dental-clinic.git
   ```
2. **Navega al directorio del proyecto:**
   ```bash
   cd dental-clinic
   ```
3. **Compila y ejecuta la aplicación** usando el wrapper de Maven (la base de datos H2 en memoria se configurará y llenará automáticamente según tus Entity Models):
   ```bash
   ./mvnw spring-boot:run
   ```
4. **Acceso a la plataforma:**
   - Abre tu navegador en: `http://localhost:8080`
   - Para probar el panel de administrador, utiliza las credenciales por defecto configuradas en `application-dev.properties`:
     - **Usuario:** `admin`
     - **Contraseña:** `admin123`

*(Nota: En el entorno de desarrollo, la consola de la base de datos H2 está habilitada y puede accederse a través de `/h2-console`).*