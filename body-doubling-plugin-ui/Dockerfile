FROM node:24 AS build
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm ci
RUN npm install -g @angular/cli
COPY . .

RUN ng build --configuration=docker
FROM nginx:alpine
COPY --from=build /usr/src/app/dist/body-doubling-plugin-ui/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
