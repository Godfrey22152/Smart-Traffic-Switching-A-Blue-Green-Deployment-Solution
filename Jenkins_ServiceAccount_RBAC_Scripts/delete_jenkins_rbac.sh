#!/bin/bash

# Define namespace
NAMESPACE="webapps"

# Delete the Secret
echo "Deleting Secret jenkins-secret in namespace $NAMESPACE..."
kubectl delete secret jenkins-secret -n $NAMESPACE

# Delete the RoleBinding
echo "Deleting RoleBinding app-rolebinding in namespace $NAMESPACE..."
kubectl delete rolebinding app-rolebinding -n $NAMESPACE

# Delete the Role
echo "Deleting Role app-role in namespace $NAMESPACE..."
kubectl delete role app-role -n $NAMESPACE

# Delete the ServiceAccount
echo "Deleting ServiceAccount jenkins in namespace $NAMESPACE..."
kubectl delete serviceaccount jenkins -n $NAMESPACE

# Verify that the resources are deleted
echo "Verifying that all resources are deleted..."
kubectl get secret jenkins-secret -n $NAMESPACE >/dev/null 2>&1
if [ $? -eq 0 ]; then
  echo "Error: Secret jenkins-secret was not deleted."
else
  echo "Secret jenkins-secret deleted successfully."
fi

kubectl get rolebinding app-rolebinding -n $NAMESPACE >/dev/null 2>&1
if [ $? -eq 0 ]; then
  echo "Error: RoleBinding app-rolebinding was not deleted."
else
  echo "RoleBinding app-rolebinding deleted successfully."
fi

kubectl get role app-role -n $NAMESPACE >/dev/null 2>&1
if [ $? -eq 0 ]; then
  echo "Error: Role app-role was not deleted."
else
  echo "Role app-role deleted successfully."
fi

kubectl get serviceaccount jenkins -n $NAMESPACE >/dev/null 2>&1
if [ $? -eq 0 ]; then
  echo "Error: ServiceAccount jenkins was not deleted."
else
  echo "ServiceAccount jenkins deleted successfully."
fi

#NOTE: If you don't want the Namespace to be deleted, comment-out this section below that handles the namespace deletion.

# Delete Namespace
echo "Deleting $NAMESPACE Namespace ..."

kubectl delete namespace $NAMESPACE >/dev/null 2>&1
if [ $? -eq 0 ]; then
  echo "$NAMESPACE Namespace and all other resources have been deleted successfully."
else
  echo "Error: $NAMESPACE Namespace Does not exist, so not deleted."
fi
