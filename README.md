# Dental Clinic Management System 🦷

Este proyecto es un sistema para uso interno de una clínica dental, diseñado para optimizar y automatizar la administración diaria del consultorio. El sistema facilitará la interacción entre el personal administrativo, los profesionales de la salud y la gestión de la información de los pacientes.

> **🚧 Estado del Proyecto:** En fase inicial de desarrollo (Setup inicial).

## 🎯 Objetivos del Proyecto (Próximas Características)

El sistema está siendo diseñado para incluir las siguientes funcionalidades principales:

- **Gestión de Usuarios y Roles:** Control de acceso diferenciado para Administradores, Odontólogos y Secretarias.
- **Gestión de Pacientes:** Registro de historial, datos personales y asignación de "Responsables" (para pacientes menores de edad).
- **Gestión de Odontólogos:** Administración de sus perfiles y horarios de trabajo.
- **Programación de Citas:** Herramienta para que las secretarias puedan agendar, reprogramar o cancelar citas, y para que los odontólogos puedan visualizar su agenda del día.
- **Catálogo de Servicios:** Administración de los tratamientos y servicios que ofrece la clínica.

## 🛠️ Tecnologías Utilizadas

Este proyecto se está construyendo con un stack moderno basado en el ecosistema Java:

**Backend:**
- [Java 21](https://jdk.java.net/21/)
- [Spring Boot 3](https://spring.io/projects/spring-boot) (Web, Data JPA, Validation)
- [Spring Security](https://spring.io/projects/spring-security)
- [H2 Database](https://www.h2database.com/) (Base de datos en memoria para el entorno de desarrollo)
- [Lombok](https://projectlombok.org/) (Reducción de código boilerplate)
- [MapStruct](https://mapstruct.org/) (Mapeo de objetos/DTOs)

**Frontend:**
- [Thymeleaf](https://www.thymeleaf.org/) (Motor de plantillas)
- HTML5, CSS3, JavaScript
- [SB Admin 2](https://startbootstrap.com/theme/sb-admin-2) (Plantilla de Bootstrap 4 para el panel de administración)

## 🚀 Cómo ejecutar el proyecto en entorno local

1. Clona este repositorio:
   ```bash
   git clone https://github.com/tu-usuario/dental-clinic.git
   ```
2. Navega al directorio del proyecto:
   ```bash
   cd dental-clinic
   ```
3. Compila y ejecuta la aplicación usando el wrapper de Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Abre tu navegador y accede a `http://localhost:8080` (la configuración actual levanta el dashboard por defecto en la raíz `/`).