ARG JENKINS_VERSION

FROM jenkins/jenkins:${JENKINS_VERSION}

USER root

RUN apt-get update && apt-get install -y docker.io && rm -rf /var/lib/apt/lists/*

RUN usermod -aG docker jenkins

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

ENV CASC_JENKINS_CONFIG=/usr/share/jenkins/casc.yaml
COPY casc.yaml /usr/share/jenkins/casc.yaml

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

USER jenkins
