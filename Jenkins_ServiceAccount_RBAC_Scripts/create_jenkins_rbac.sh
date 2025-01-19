#!/bin/bash

# Define namespace
NAMESPACE="webapps"

# Check if namespace exists, if not, create it
kubectl get namespace $NAMESPACE >/dev/null 2>&1
if [ $? -ne 0 ]; then
  echo "Namespace $NAMESPACE does not exist. Creating namespace..."
  kubectl create namespace $NAMESPACE
else
  echo "Namespace $NAMESPACE already exists."
fi

# Create the resources using the jenkins_rbac_config.yaml file
echo "Creating Jenkins ServiceAccount, Role, RoleBinding, and Secret..."
kubectl apply -f jenkins_rbac_config.yaml

# Verify the creation of the ServiceAccount, Role, and RoleBinding
kubectl get serviceaccount jenkins -n $NAMESPACE
kubectl get role app-role -n $NAMESPACE
kubectl get rolebinding app-rolebinding -n $NAMESPACE

# Wait for the secret to be created
echo "Waiting for the secret to be created..."
sleep 5

# Get the secret for the service account and display it
SECRET_NAME=$(kubectl get secret -n $NAMESPACE | grep jenkins-secret | awk '{print $1}')

if [ -z "$SECRET_NAME" ]; then
  echo "Error: Could not find the secret for the ServiceAccount jenkins."
else
  echo "ServiceAccount secret created: $SECRET_NAME"

  # Retrieve the secret token
  SECRET_TOKEN=$(kubectl get secret $SECRET_NAME -n $NAMESPACE -o jsonpath='{.data.token}' | base64 --decode)

  if [ -z "$SECRET_TOKEN" ]; then
    echo "Error: Could not retrieve the token from the secret."
  else
    echo "Secret token for the ServiceAccount jenkins:"
    echo "$SECRET_TOKEN"
  fi
fi

echo "Jenkins service account and RBAC resources created successfully!"
