FROM clojure
RUN mkdir -p /opt/todoapp
WORKDIR /opt/todoapp
COPY project.clj /opt/todoapp
RUN lein deps
COPY . /opt/todoapp

EXPOSE 3000

RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" todoapp-standalone.jar
CMD ["java", "-jar", "todoapp-standalone.jar"]