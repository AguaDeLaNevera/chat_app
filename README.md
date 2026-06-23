Aplicación de Chat Stateless (Spring Boot + Docker Swarm)
Descripción general

Este proyecto es un backend de chat grupal en tiempo real de tipo simple, desarrollado con Spring Boot, desplegado usando Docker Swarm y respaldado por PostgreSQL.

El sistema está diseñado para ser stateless (sin estado) y horizontalmente escalable, lo que significa que múltiples réplicas de la aplicación pueden ejecutarse simultáneamente sin afectar la consistencia.

Tecnologías utilizadas:
- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL 16
- Docker
- Docker Swarm
- Frontend en JavaScript puro (Vanilla JS)

Como ejecutar el proyecto:
ya que están los comandos en el Dockerfile para hacer el .jar nos saltamos este paso.
- Primer paso: "docker build -t chat-app ."
- Segundo paso: "docker stack deploy -c compose.yaml chat-stack"

Con esto ya tenemos todo preparado.

Endpoints de la API "/chat"

Mensajes

GET "/chat/messages"

POST "/chat/message"

{

"username": "nombre de usuario",

"userId": "1",

"id": "1",

"content": "el texto del mensaje va aquí"

}

Usuarios

GET "/chat/users"

GET "/chat/users/{id}"

POST "/chat/user"

{

"id": "1"

"username": "Juan"

}

Estructura del proyecto

src/

├── application

├── domain

├── infrastructure

compose.yaml

Dockerfile

README.md