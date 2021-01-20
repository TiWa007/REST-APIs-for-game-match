# REST APIs for game match

REST APIs to match users based on their interests for a centralized app of all games. 
Main features include:
- User and interest management 
- Game auto-matching based on user geography, interest game and interest level
- Search maximum credits based on user interest game and level

### Built With
- [Java](https://www.java.com/en/) - Java 8
- [Spring Boot](https://spring.io/projects/spring-boot) - Spring Boot REST API
- [H2 Database](https://www.h2database.com/html/main.html) - In-memory database
- [Maven](https://maven.apache.org/) - Project management 

## Design

- [Architecture](#architecture)
- [REST API Design](#rest-api-design)
- [Datamodel](#datamodel)
- [Unit testing](#unit-testing)

#### Architecture
The below image shows the high level architecture of the backend server.
- **Client**: The client side send HTTP request to backend server. The data is transmitted using JSON format.
- **Controller layer**: The controller layer implements REST API.
- **Data access layer**: The data access layer implements Spring Data JPA.
- **Data storage** The storage layer of the application uses in-memory H2 Database, a relational database
- **Common**: The Common component contains utility code (exception handling, configurations, etc.) used across the application.
\
Package overview:
- **`gamematchrestapi.controller`**: Provides the REST API.
- **`gamematchrestapi.entity`**: Classes that represent persistable entities.
- **`gamematchrestapi.repository`**: Classes performs CRUD (Create, Read, Update, Delete) operations and act as the bridge to the H2 Database.
- **`gamematchrestapi.exception`**: Contains custom exceptions.
- **`gamematchrestapi.config`**: Classes for the configuration of Swagger 3 and CORS.

![High Level Architecture](docs/images/highlevelArchitecture.png)

#### REST API Design
The following tables shows the design of the REST API. 
![REST API Design1](docs/images/RESTAPI1.png)
![REST API Design2](docs/images/RESTAPI2.png)

#####Policies

API for creating:
+ Attempt to create an entity with invalid data: Throws `MethodArgumentNotValidException`.

API for retrieving:
+ Attempt to retrieve an entity that does not exist: Throws `ResourceNotFoundException`.
+ Attempt to retrieve an entity with invalid request parameter: Throws `InvalidRequestException`.

API for updating:
+ Attempt to update an entity that does not exist: Throws `InvalidRequestException`.
+ Attempt to update an entity with invalid data: Throws `MethodArgumentNotValidException`.

API for deleting:
+ Attempt to delete an entity that does not exist: Throws `InvalidRequestException`.
+ Cascade policy: When a parent entity is deleted, entities that have referential integrity with the deleted entity should also be deleted.

API usage was documented using ``Swagger 3`` and the document is available at http://localhost:8080/swagger-ui/#/ 
when the server is running.

#### Datamodel
There are two entities: `User` and `Interest`. Entity `User` has name, gender, nickname and geography attributes. 
Entity `Interest` has game, level, credit attributes. Entity `User` have an one-to-many relationship with Entity `Interest`.

![Datamodels](docs/images/Datamodels.png)

To ensure they are in a valid state, data was validated inside entities before creating/updating them. 
[Hibernate Validator](http://hibernate.org/validator/) and custom validator (`@InStringArray`) were used to 
validate application constraints as shown in the following table.

![Input validator](docs/images/validator.png)


#### Unit testing

- The controller classes was tested with Spring Boot and ``@WebMvcTest``
- In the data access layer, JPA Queries of the repository classes was tested with Spring Boot and ``@DataJpaTest``
- Integration Tests with ``@SpringBootTest``


##### Build the Project

``
mvn clean install
``

##### Run and test the service
To start the server, type ``
mvn spring-boot:run 
`` in the terminal from the root project directory. You can
change server port number by typing 
``
mvn spring-boot:run -Drun.arguments="--server.port=8080"
``. To exit the server, press ``ctrl-c``.\
\
To test the server, open a web browser to http://localhost:8080/swagger-ui/#/ using Swagger 3.0.

