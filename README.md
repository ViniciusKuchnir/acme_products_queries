<h1>Product Querie Microservice Project</h1>

<h2>Description</h2>

<p>This project was developed as part of the Software Architecture course at the Instituto Superior de Engenharia do Porto (ISEP). The main objective of the project was to build a microservice dedicated to handling product-related queries, following modern software architecture practices.</p>

<h2>Main features</h2>

<ul>
  <li><strong>Product Querie Microservice:</strong> The core of the project consists of a service specialized in carrying out product-related queries. This includes operations such as searching, filtering and obtaining details.</li>
  <li><strong>Pattern CQRS (Command Query Responsibility Segregation):</strong> The project adopts the CQRS pattern to separate the command logic from the query logic. This promotes the system's scalability and flexibility, allowing specific optimizations for each type of operation.</li>
  <li><strong>Messaging for Communication with Other Microservices:</strong> Communication between microservices is carried out via messaging, promoting an asynchronous and decoupled architecture. This facilitates integration with other services in the system's infrastructure.</li>
  <li><strong>Database-per-service pattern:</strong> Each microservice has its own dedicated database. This promotes the independence and autonomy of the services, avoiding direct coupling between them.</li>
  <li><strong>REST API for External Communication:</strong> The microservice provides a REST API to allow interaction with the outside world. This facilitates integration with front-end applications, other systems and external clients.</li>
</ul>

<h2>Technologies used</h2>

<ul>
  <li><strong>Programming Language:</strong> Java</li>
  <li><strong>Framework:</strong> Spring Boot</li>
  <li><strong>Database:</strong> CSV</li>
  <li><strong>Messaging system:</strong> RabbitMQ</li>
</ul>

<h2>Author</h2>

<ul>
  <li>Vinícius Kuchnir Rodrigues Pinto</li>
</ul>

<h2>Acknowledgements</h2>

<p>I would like to thank Professors Nuno Alexandre Pinto da Silva and Joana Filipa Ferreira Carneiro dos Santos from ISEP for their guidance and support during the development of this project.</p>

