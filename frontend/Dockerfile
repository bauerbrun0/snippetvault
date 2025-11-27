# 1. Build stage
FROM node:20-alpine AS builder

WORKDIR /app

RUN apk add --no-cache curl bash && \
    curl -fsSL https://bun.sh/install | bash

ENV PATH="/root/.bun/bin:${PATH}"

COPY package.json bun.lock ./
RUN bun install

COPY . .

RUN bun run build

RUN cp dist/index.html dist/index.html.template

# 2. Runtime stage
FROM nginx:stable-alpine

# Install envsubst (for runtime env var replacement)
RUN apk add --no-cache gettext

WORKDIR /usr/share/nginx/html

COPY --from=builder /app/dist ./
COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["sh", "-c", "envsubst < /usr/share/nginx/html/index.html.template > /usr/share/nginx/html/index.html && nginx -g 'daemon off;'"]
