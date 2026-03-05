FROM nginx:alpine

RUN echo '<!DOCTYPE html><html><body><h1>Jenkins Docker Test OK!</h1></body></html>' > /usr/share/nginx/html/index.html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
