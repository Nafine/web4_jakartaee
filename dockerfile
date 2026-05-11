FROM eclipse-temurin:21-jdk AS builder

WORKDIR /workspace

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle

COPY src ./src

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon libertyPackage

FROM gitlab-cr.dc.lysmux.dev/nafine/web4_jakartaee:kernel-slim-java21-openj9-ubi-minimal

USER root

COPY --chown=1001:0 --from=builder /workspace/src/main/liberty/config/server.xml /config/
COPY --chown=1001:0 --from=builder /workspace/build/wlp/usr/shared/resources/postgresql-*.jar /opt/ol/wlp/usr/shared/resources/
RUN features.sh
COPY --chown=1001:0 --from=builder /workspace/build/libs/*.war /config/apps/
RUN configure.sh

USER 1001

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
    CMD curl -fsS http://localhost:9080/web4/ || exit 1
