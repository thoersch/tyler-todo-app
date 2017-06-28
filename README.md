## Description

This is an app to track your daily "todos". The todos are persisted the project is setup to be easily deployed to a remote machine via Docker.

## Why?

This project represents a culmination of technologies that I wanted to explore, noteably FB's react framework, Clojurescript, and Docker. While the app is trivial, it does work in a RESTful architecture with a postgres persistence layer (using the wonderful Korma Clojure library).

## If you were so inclined to run this project

 * Create the docker image `docker build -t account/todoapp:tag .` replacing the account and tag as desired
 * Update the `docker-compose.yml` file to coordinate the todo services's account and tag you chose from last step
 * Run `docker-compose up` in the same directory as the `docker-compose.yml`
 * Open an Internet browser to port `http://127.0.0.1:3000`

Special note: if you use a docker-machine to host on a vm or some other remote box, you'll need to create a directory `./data` which is where postgres will use to store it's persisted data