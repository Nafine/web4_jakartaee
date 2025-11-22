FROM icr.io/appcafe/open-liberty:kernel-slim-java21-openj9-ubi-minimal

USER root

COPY --chown=1001:0 build/wlp/usr/shared/resources/postgresql-42.7.7.jar /liberty/usr/shared/resources/
COPY --chown=1001:0 src/main/liberty/config/server.xml /config/
RUN features.sh
COPY --chown=1001:0 build/libs/*.war /config/apps/
RUN configure.sh
USER 1001