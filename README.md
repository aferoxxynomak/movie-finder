# Movie Finder (Java)
A simple movie finder and information retrieval tool.

## The project
The command line (Spring Boot) application displays data about the movie requested by the user. After starting, we can choose from three options in the menu:
1. Find movie information by name - You can enter a movie name and it fetch the ID, name, score and genres in a table
2. Find Wikipedia details for a movie by ID - Enter a movie ID (fetchd by the first menu option) and it gather wikipedia's first paragraph and URL
3. Find related movies by ID - A movie ID must be entered again, then show the related (similar) movies in a table (ID, name)

## Requirements
* Java: min. 14 (builded with 19), set the JAVA_HOME
* Maven
* Optional: Docker

## Build
In project directory:
`mvnw clean package`

## Run
### Method I.
Run with Spring Boot.

In project directory:
`mvnw spring-boot:run`

### Method II.
Full experience without on screen log.

In project directory's target folder after build:
`java -jar movie-finder-1.0.0.jar`

### Method III.
Build and run with Docker
In project directory:

`docker build -t movie-finder .`

`docker run -it --name movie-finder movie-finder`

### Method IV.
Pull and run from DockerHub.

`docker pull aferoxxynomak/movie-finder`

`docker run -it --name movie-finder aferoxxynomak/movie-finder`