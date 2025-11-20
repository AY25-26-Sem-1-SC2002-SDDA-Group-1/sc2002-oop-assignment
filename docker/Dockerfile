# Internship Placement System CLI Dockerfile
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy all Java source files and data
COPY ../*.java ./
COPY ../data ./data

# Compile the Java application
RUN javac *.java

# Run the CLI application
ENTRYPOINT ["java", "InternshipPlacementSystem"]
