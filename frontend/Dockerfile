# Agendaty Frontend - Dockerfile multietapa
FROM node:20-alpine AS build
WORKDIR /build
# Vite incorpora estas variables al bundle durante el build
ARG VITE_API_URL=http://localhost:8080
ARG VITE_USE_MOCK=false
ENV VITE_API_URL=$VITE_API_URL
ENV VITE_USE_MOCK=$VITE_USE_MOCK
COPY package.json package-lock.json* ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
