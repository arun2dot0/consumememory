# Getting Started

Application to demo HPA with Kubernetes . 
Spring application just consumes memory on api calls . Also for scale
down there is a scheduler that frees up the memory every 60 seconds . 
It will test the kubernetes pods scale up as number of api request increase and scale down 
as the request go down . 

![Screenshot 2024-07-27 at 10 14 52 PM](https://github.com/user-attachments/assets/64bec543-180a-478c-bc35-01dfc25369ae)

using hey testing tool for high number of api calls required to scale up 

Main configuration for Scale up and Scale down is in hpa.yaml

### Build the Image
```
./gradlew clean build  
docker build -t consumememory-app:1.0 .  
docker run --rm -p 8080:8080 -e JAVA_OPTS="-Xms256m -Xmx512m" consumememory-app:1.0 
curl  http://localhost:8080/consume-memory ( in other window ) 
curl  http://localhost:8080/clear
```
### Kubernetes deployment 
Used minikube with tunnel option so that I can use the loadbalancer. 
Tunnel is good for development , testing with applications with high network usage it may not
be desired . 

Minikube steps using LoadBalancer Option 
```
minikube start 
minikube image load consumememory-app:1.0
minikube addons enable metrics-server
minikube tunnel ( Run in a new window and keep it open)
```

Kubernetes deployment
```
cd deployment

kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f hpa.yaml

```

### Check every thing is up

```
kubectl get pods
kubectl get services 
kubectl get hpa
```



### Simple test 
Use the url for testing
```
curl http://localhost:8080/consume-memory

for nodeport get the url and test
minikube service consumememory-app-service --url

```
now its serverd from k8s 

###Test scale

I am using hey for testing ,you can also use ab test 
checkout options here https://github.com/rakyll/hey

Running 500 request  with 20 workers with rate limit of 40 per second 
```
hey -n 500 -c 20 -q 40 "http://localhost:8080/consume-memory"
```
Keep running again if you want to see scale up to maximum 

then you can clear using curl to test scale down

```hey -n 100 -c 1 -q 10 "http://localhost:8080/clear" ```
Runing multiple times as it has to hit different pods 

###Test ScaleUp and ScaleDown (as test completes)
```
execute all of them in different tabs to watch 

kubectl get hpa consumememory-app-hpa --watch
watch -n 1 kubectl get pods
watch -n 1 kubectl top pods
watch -n 1 kubectl describe hpa consumememory-app-hpa
```
Scale up as the test runs
![Screenshot 2024-07-27 at 10 04 26 PM](https://github.com/user-attachments/assets/3178fc40-f5b4-4ce8-97e4-f7b7913b7693)

Scale down as tests complete , there is a scheduler in spring application to free up every 60 seconds

### cleanup
```
kubectl delete deployment consumememory-app
kubectl delete service consumememory-app-service
kubectl delete hpa consumememory-app-hpa
```


### Debugging
if metrics server is not installed
```
kubectl describe hpa consumememory-app-hpa ( get hpa details and info on scale up and scale down ) 

kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml  
```
and restart k8s

Restart deployment for any changes
```
kubectl rollout restart deployment consumememory-app
```
