NAME = excord
VERSION=1.0-SNAPSHOT

all: build publish

build:
	mvn clean install
	cp application.properties docker
	cp target/$(NAME)-$(VERSION).jar docker
	docker build -t $(NAME):$(VERSION) --no-cache --rm docker
	rm -f docker/$(NAME)-*.jar
	rm -f docker/application.properties

publish:
	docker tag $(NAME):$(VERSION) localhost:5000/$(NAME):$(VERSION)
	docker tag $(NAME):$(VERSION) localhost:5000/$(NAME):latest
	docker push localhost:5000/$(NAME)

up:
	docker-compose -f ./docker/docker-compose.yml up -d

down:
	docker-compose -f ./docker/docker-compose.yml down