#!/bin/sh

## Git Repository
# git clone https://github.com/blindchaser/gcp-hello-world.git

## Login & credentials
# gcloud init
# 2
# Y
# Login to the qwiklabs account
# 7 (or whichever the project is)

# ## Environment Variables
USERNAME=$(gcloud config list account --format "value(core.account)")
PROJECT_ID=$(gcloud config get-value project)
PROJECT_NUMBER="$(gcloud projects describe ${PROJECT_ID} --format='get(projectNumber)')"

sed -i -e "s/YOUR_PROJECT_ID/$PROJECT_ID/g" kubernetes/k8sconfig.yaml src/main/resources/application.yaml

# ## Virtual machine creation and connection
# gcloud compute instances create gcp-demo-vm \
#   --image-family=rhel-8 \
#   --image-project=rhel-cloud \
#   --zone=us-central1-a
# gcloud compute ssh gcp-demo-vm

## Spanner instance and database creation
gcloud spanner instances create gcp-demo-spanner-instance \
  --config=regional-us-central1 \
  --description="gcp-demo-spanner-instance" \
  --nodes=1
gcloud spanner databases create gcp-demo-db \
  --instance=gcp-demo-spanner-instance

## IAM & Service Accounts
gcloud spanner databases add-iam-policy-binding gcp-demo-db \
  --instance="gcp-demo-spanner-instance" \
  --member="user:$USERNAME" \
  --role="roles/spanner.admin"
gcloud iam service-accounts keys create src/main/resources/credential.json \
  --iam-account "$PROJECT_ID@$PROJECT_ID.iam.gserviceaccount.com"

## Build, Run, & Ping endpoint to populate database
mvn clean install
# java -jar target/gcp-demo-0.0.1-SNAPSHOT.jar
# curl -v http://localhost:8080/update

## Repo
gcloud source repos create gcp-demo-repo
git remote add google https://source.developers.google.com/p/$PROJECT_ID/r/gcp-demo-repo

# Create GKE cluster and add IAM policy binding
gcloud container clusters create gcp-demo-cluster \
  --num-nodes 1
  --zone us-central1-a
gcloud projects add-iam-policy-binding ${PROJECT_NUMBER} \
  --member=serviceAccount:${PROJECT_NUMBER}@cloudbuild.gserviceaccount.com \
  --role=roles/container.developer

# Create trigger & push to run trigger
gcloud beta builds triggers create cloud-source-repositories \
  --repo=gcp-demo-repo \
  --branch-pattern=".*" \
  --build-config=cloudbuild.yaml

git add . && git commit -m 'cloud build commit'
# I have to do the rest manually because it keeps making me reset some credential for google's git
# git push google

# # Clean up
# rm kubernetes/k8sconfig.yaml-e
# rm src/main/resources/application.yaml-e
# rm src/main/resources/credential.json
# sed -e '19s|.*|          image: gcr.io\/YOUR_PROJECT_ID/gcp-demo-image:latest|' -i '' kubernetes/k8sconfig.yaml
# sed -e '9s|.*|      project-id: YOUR_PROJECT_ID|' -i '' src/main/resources/application.yaml
# git reset --soft HEAD~
# git remote remove google
# gcloud auth revoke $USERNAME
