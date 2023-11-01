# Setting up Jenkins with Docker

The setup of Jenkins with Docker follows this
guide [Installing Jenkins with Docker](https://www.jenkins.io/doc/book/installing/docker/).

Jenkins is isolated in a Docker container. Since I'm on Windows, I've followed the steps provided for Windows users.

## Steps to Set Up Jenkins on Docker

1. Create a bridge network in Docker:

    ```shell
    docker network create jenkins
    ```

2. Run a `docker:dind` Docker image:

    ```shell
    docker run --name jenkins-docker --rm --detach ^
    --privileged --network jenkins --network-alias docker ^
    --env DOCKER_TLS_CERTDIR=/certs ^
    --volume jenkins-docker-certs:/certs/client ^
    --volume jenkins-data:/var/jenkins_home ^
    --publish 2376:2376 ^
    docker:dind
    ```

    3. Customize the official Jenkins Docker image with the following steps:

       a. Create a Dockerfile with the following content:

       ```dockerfile
            FROM jenkins/jenkins:2.414.2-jdk17
            USER root
            RUN apt-get update && apt-get install -y lsb-release
            RUN curl -fsSLo /usr/share/keyrings/docker-archive-keyring.asc \
            https://download.docker.com/linux/debian/gpg
            RUN echo "deb [arch=$(dpkg --print-architecture) \
            signed-by=/usr/share/keyrings/docker-archive-keyring.asc] \
            https://download.docker.com/linux/debian \
            $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list
            RUN apt-get update && apt-get install -y docker-ce-cli
            USER jenkins
            RUN jenkins-plugin-cli --plugins "blueocean docker-workflow"
       ```

       b. Build a new Docker image from this Dockerfile and assign the image a meaningful name, e.g., "
       jenkins-docker-v213":

        ```shell
        docker build -t <name of the image> .
        ```

4. Run your own `<name of the image>` image as a container in Docker using the following `docker run` command:

    ```shell
    docker run --name jenkins-blueocean --restart=on-failure --detach ^
    --network jenkins --env DOCKER_HOST=tcp://docker:2376 ^
    --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 ^
    --volume jenkins-data:/var/jenkins_home ^
    --volume jenkins-docker-certs:/certs/client:ro ^
    --publish 8081:8080 --publish 50000:50000 <name of the image>
    ```

   In my case, I changed `8080:8080` to `8081:8080` to host it on `8081` on the local machine because the main
   application runs on port `8080`.

5. Open Jenkins on localhost at the specified port. Check if the container is running with `docker ps`. If it's not
   running, start it with `docker start <name of the image>`.

6. The first time Jenkins is opened, it will ask to set up. Follow this
   guide [Docker Setup Wizard](https://www.jenkins.io/doc/book/installing/docker/#setup-wizard) as it is
   straightforward. Note: to get the admin password, run `docker logs <name of the image>`.

7. You are set up! Create an account and you should be good to create CI/CD workflows.

# Set up Jenkins Pipeline

Setting up a Jenkins pipeline is fairly easy. Let's go through the process step by step. After following all the steps mentioned above, log in and follow these instructions:

1. Click on `New Item` and add a new item with the type `Pipeline`. Give it a meaningful name.

2. In the `Pipeline` section, select `Pipeline from SCM` and paste the Git repository URL for which you want to create a pipeline.

3. Before proceeding, generate a Deploy Key for the GitHub repository at the repository level. Add this Deploy Key to Jenkins as credentials for SSH. Note: GitHub API deprecated HTTPS authentication with username and password.

4. After completing the above steps, go back to the pipeline setup. Select the credentials you added, and it should connect to the repository.

5. In the repository, create a file named `Jenkinsfile`. This file will contain the definition of your pipeline script.

Following these steps will help you set up a Jenkins pipeline for your project.
