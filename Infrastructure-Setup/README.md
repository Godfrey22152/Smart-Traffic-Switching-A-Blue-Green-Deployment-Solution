# Smart Traffic Switching: A Blue-Green Deployment Solution Using CI/CD Automation

## Infrastructure Setup

### CI: AWS EC2 Instance for Jenkins, SonarQube, and Nexus


#### Manual Procedure for AWS EC2 Instance setup for Jenkins, SonarQube, and Nexus

##### First Option: Using AWS Management Console

Set up three AWS EC2 instances for Jenkins, SonarQube, and Nexus.
Launch 3 EC2 instances (Ubuntu) in AWS but ensure you add these user-data for each instance during creation to install necessary dependencies.

- **User Data for EC2 Instance dependency installation**:

To add the **User Data** script during the EC2 instance creation, follow these steps:

   - **Log in to AWS Management Console** and navigate to **EC2**.
   - Click on **Launch Instance**.
   - Choose the **Amazon Machine Image (AMI)** and **Instance Type**, then proceed to **Configure Instance Details**.
   - Scroll down to the **Advanced Details** section.
   - In the **User data** field, add your script or commands:
   
   - **For Jenkins:**
   ```bash  
    #!/bin/bash

    # Update the package list
    sudo apt-get update -y

    # Install Java
    sudo apt-get install -y openjdk-17-jre-headless

    # Jenkins installation process
    echo "Installing Jenkins package..."
    sudo wget -O /usr/share/keyrings/jenkins-keyring.asc https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
    echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null
    sudo apt-get update -y
    sudo apt-get install -y jenkins

    # Docker installation
    echo "Installing Docker..."
    sudo apt-get install -y docker.io
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo usermod -aG docker $USER
    sudo chmod 666 /var/run/docker.sock

    sudo usermod -aG docker jenkins

    # Trivy installation
    echo "Installing Trivy..."
    wget https://github.com/aquasecurity/trivy/releases/download/v0.27.1/trivy_0.27.1_Linux-64bit.deb
    sudo dpkg -i trivy_0.27.1_Linux-64bit.deb
   ```

   - **For SonarQube:**
   ```bash
    #!/bin/bash
    sudo apt-get update

    ## Install Docker
    yes | sudo apt-get install docker.io
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo usermod -aG docker $USER
    sudo chmod 666 /var/run/docker.sock
    echo "Waiting for 30 seconds before runing Sonarqube Docker container..."
    sleep 30

    ## Runing Sonarqube in a docker container
    docker run -d -p 9000:9000 --name sonarqube-container sonarqube:lts-community
   ```
   - **For Nexus:**
   ```bash
    #!/bin/bash
    sudo apt-get update

    ## Install Docker
    yes | sudo apt-get install docker.io
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo usermod -aG docker $USER
    sudo chmod 666 /var/run/docker.sock
    echo "Waiting for 30 seconds before running Nexus Docker container..."
    sleep 30

    ## Runing Nexus in a docker container
    docker run -d -p 8081:8081 --name nexus-container sonatype/nexus3:latest
   ```

   - Continue with the remaining steps to configure **Storage**, **Security Groups**, and **Tags**, then launch the instance.


##### Second Option: Using AWS CLI to setup the EC2 Instances
You can also use the `AWS CLI` to setup each of the EC2 Instance:

   ```bash
    aws ec2 run-instances --image-id ami-12345678 --instance-type t2.medium --key-name your-key-pair \
    --security-group-ids sg-12345678 --subnet-id subnet-12345678 --user-data file://userdata.sh
   ```

- **NOTE**:
 - `--image-id ami-12345678`: Specifies the Amazon Machine Image (AMI) ID to use for the instance. This ID represents the OS and software configuration of the instance (e.g., Ubuntu, Amazon Linux).

 - `--instance-type t2.medium`: Specifies the type of instance to create, which determines its CPU, memory, and network performance. `t2.medium` provides moderate resources suitable for small to medium workloads. (You should increase the size for Jenkins instance.)

 - `--key-name your-key-pair`: Specifies the name of the key pair to use for SSH access to the instance. You should have created this key pair in advance within AWS EC2.

 - `--security-group-ids sg-12345678`: Assigns the instance to a security group that controls inbound and outbound traffic rules. Security groups are configured with rules to allow or restrict network access.

 - `--subnet-id subnet-12345678`: Specifies the subnet where the instance will be launched. The subnet should be part of a Virtual Private Cloud (VPC) in which the instance can access resources and network configurations.

 - `--user-data file://userdata.sh`: Supplies the user data script (`userdata.sh` in this case) that will run automatically when the instance starts. If the userdata.sh is in the same directory where you’re running the command, you can reference it as `file://userdata.sh`. Otherwise, provide the full path, e.g., `file:///home/user/scripts/userdata.sh`.


#### Automated setup for the AWS EC2 Instance for Jenkins, SonarQube, and Nexus
 For a fully automated setup of the EC2 Instances using Terraform: 
 Refer to the [Terraform EC2 Setup](https://github.com/Godfrey22152/automation-of-aws-infra-using-terraform-via-Gitlab) for automated setup scripts.


### Configure Installed Tools: `Jenkins`, `SonarQube`, and `Nexus`

### Jenkins
 
Access running `Jenkins` and complete the setup over the browser using the EC2 Instance server `Public IP` at `http://<public-ip>:8080`.
   
- **Access Jenkins Initial Admin Password**:

   ```bash
   sudo cat /var/jenkins_home/secrets/initialAdminPassword
   ```

- **create an account to sign in and Install recommended Plugins**

### SonarQube

- **Access running SonarQube server at `http://<public-ip>:9000`**
- **SonarQube Initial Admin Password Credentials**:
  - Username: `admin`
  - Password: `admin`


### Nexus 
- **Access running Nexus server at `http://<public-ip>:8081`**  
- **Access Nexus Credentials**:
  The default admin password is stored in a file inside the container. Retrieve it by accessing container shell:

     ```bash
     docker exec nexus-container cat /nexus-data/admin.password
     ```


### CD: EKS Cluster Setup
Refer to the repository and guide **[here](https://github.com/Godfrey22152/Automated-EKS-Cluster-Deployment-Pipeline/tree/main/aws_eks_terraform_files)** for setting up your EKS cluster using terraform. 
 
- **Before you run the terraform scripts**: 
Ensure you have `terraform`, `AWS CLI`, and `kubectl` installed on the server which you will use to create the cluster:

```bash
# Terraform Installation
echo "Installing Terraform..."
wget https://releases.hashicorp.com/terraform/1.6.5/terraform_1.6.5_linux_386.zip
sudo apt-get install -y unzip
unzip terraform_1.6.5_linux_386.zip
sudo mv terraform /usr/local/bin/
```
```bash
# Install Kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl.sha256"
echo "$(cat kubectl.sha256)  kubectl" | sha256sum --check
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl 
```
```bash
# Install AWS CLI 
sudo apt install unzip 
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

- **After Installation, Run**: 
```bash
aws configure
# Follow the prompts and provide your "Access key ID" and "Secret Access Key" 
```


**You can also refer to this repository and guide [here](https://github.com/Godfrey22152/Automated-EKS-Cluster-Deployment-Pipeline.git) for automated cluster creation.**


**After setting up, connect to the cluster with**:
```bash
aws eks --region <region> update-kubeconfig --name <cluster-name>

# In our case  
aws eks --region eu-west-1 update-kubeconfig --name odo-eks-cluster
```

### Service Account & Secrets for Jenkins
Create a `service account` in the `webapps` `Namespace` and necessary `secrets` for Jenkins to connect to the EKS cluster:
Use the provided scripts in the **[Jenkins_ServiceAccount_RBAC_Scripts](./Jenkins_ServiceAccount_RBAC_Scripts)** folder for automating service account and secret creation. You can refer to the detailed `README.md` inside the folder.
- Run the automated script `create_jenkins_rbac.sh` available in the `Jenkins_ServiceAccount_RBAC_Scripts` folder in the repository.
- After creating the secrets, copy the secret as displayed on the screen, add them to Jenkins by navigating to `Manage Jenkins` > `Credentials`. (Select `secret text` and paste the secret)

---
### Complete Kubernetes Cluster Setup

#### install helm 
   ```bash
   curl -o /tmp/helm.tar.gz -LO https://get.helm.sh/helm-v3.10.1-linux-amd64.tar.gz
   tar -C /tmp/ -zxvf /tmp/helm.tar.gz
   mv /tmp/linux-amd64/helm /usr/local/bin/helm
   chmod +x /usr/local/bin/helm
   ```

#### Install Nginx ingress controller.
   You can find the Kubernetes NGINX documentation **[here](https://kubernetes.github.io/ingress-nginx/)**
   First thing we do is check the compatibility matrix to ensure we are deploying a compatible version of NGINX Ingress on our Kubernetes cluster.

   The Documentation also has a link to the **[GitHub Repo](https://github.com/kubernetes/ingress-nginx/)** which has a compatibility matrix.

 - **Get the installation YAML**
   The controller ships as a `helm` chart, so we can grab version `v1.11.3` as per the compatibility matrix.

   From our container we can do this:

   ```bash
   helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
   helm search repo ingress-nginx --versions
   ```

   From the app version we select the version that matches the compatibility matrix.

   ```bash
   NAME                            CHART VERSION   APP VERSION     DESCRIPTION
   ingress-nginx/ingress-nginx     4.11.3          1.11.3          Ingress controller for Kubernetes using NGINX a...
   ```
   Now we can use helm to install the chart directly if we want.
   Or we can use helm to grab the manifest and explore its content.
   We can also add that manifest to our git repo if we are using a GitOps workflow to deploy it.

   ```bash
   CHART_VERSION="4.4.0"
   APP_VERSION="1.5.1"

   mkdir ./nginx-ingress-manifests

   helm template nginx-ingress ingress-nginx \
   --repo https://kubernetes.github.io/ingress-nginx \
   --version ${CHART_VERSION} \
   --namespace ingress-nginx \
   --set controller.service.type=LoadBalancer \
   > ./nginx-ingress-manifests/installation_nginx_ingress.${APP_VERSION}.yaml
   ```
 - **Deploy the Ingress controller**
   ```bash
   kubectl create namespace ingress-nginx
   kubectl apply -f ./nginx-ingress-manifests/installation_nginx_ingress.${APP_VERSION}.yaml
   ```

 - **Check the installation**
   ```bash
   kubectl get pods -n ingress-nginx
   ```
   ```bash
   NAME                                                          READY   STATUS    RESTARTS      AGE
   pod/nginx-ingress-ingress-nginx-controller-6ffbff94df-td7pr   1/1     Running   0             5m
   ```
 - **Check the Nginx Ingress Service**

   ```bash
   kubectl get svc -n ingress-nginx
   NAME                                               TYPE           CLUSTER-IP      EXTERNAL-IP      PORT(S)                      AGE
   nginx-ingress-ingress-nginx-controller             LoadBalancer   10.105.156.69   XXXXXXXXXXXXXX   80:31507/TCP,443:31539/TCP   5m
   nginx-ingress-ingress-nginx-controller-admission   ClusterIP      10.98.183.68    <none>           443/TCP                      5m
   ```
   
