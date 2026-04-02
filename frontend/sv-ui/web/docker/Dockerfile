# ----------- BUILD STAGE -----------
    FROM node:22.0.0-alpine AS build

    RUN apk add --no-cache git>2.30.0
    
    ARG WORK_DIR
    WORKDIR /app
    
    # Keep safe memory limit (DO NOT use 4096 on 4GB container)
    ENV NODE_OPTIONS="--max-old-space-size=3072"
    
    # Copy code
    COPY ${WORK_DIR} .
    RUN ls -lah
    
    
    # Move to web app
    WORKDIR /app/web
    
    # Install dependencies ONLY ONCE (important for hoisting)
    RUN yarn install --frozen-lockfile
    
    # Build internal packages (no root deletion)
    RUN ./install-deps.sh
    
    # Clean cache (reduces memory spikes)
    RUN rm -rf node_modules/.cache
    
    # Build app
    RUN yarn build
    
    
    # ----------- PRODUCTION STAGE -----------
    FROM nginx:mainline-alpine
    
    ENV WORK_DIR=/var/web/sv-ui
    
    RUN mkdir -p ${WORK_DIR}
    
    COPY --from=build /app/web/build ${WORK_DIR}/
    COPY --from=build /app/web/docker/nginx.conf /etc/nginx/conf.d/default.conf