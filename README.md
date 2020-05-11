# GCP Education Initiative

This is a walkthrough of the usage of the GCP tools we've learned to use along
with our example code. The walkthrough will instruct you how to use the UI to
comeplete the tasks, but the equivalent gcloud command will come after each
step. Feel free to only use the CLI or UI as you prefer, but I do recommend
using the CLI.

## Time Requirement: 1 hour - 2 hours

By the end of this walkthrough, you will:

* Know how to create a GCP Compute Engine VM to your specifications and
  connect to it.
* Create a GCP Spanner instance, database, and table and populate it with data
  from our service using a IAM Service Account.
* Create a GCP GKE Cluster and use a trigger to have Cloud Build run the
  pipeline for you when you push to Cloud Source Repository.

## Steps

### Step 1: Prerequisites

You will need a [Qwiklab CE account](https://ce.qwiklabs.com/) to run this in
your own (free!) GCP Environment, which is the first lab in the Sabre GCP
training track. If you see an error message for a quota exceeded, just message
support using the blue chat bubble that floats on the bottom right of the
screen and ask them to help you resolve the issue. They should either increase
or remove the quota.

You will also need the Google Cloud SDK. If you have a mac, I reccommend
installing with brew: `brew cask install google-cloud-sdk`. If you have a PC,
I would use their
[download link](https://cloud.google.com/sdk/docs/downloads-versioned-archives).

### Step 2: Set-Up

First, clone our repo to your local machine and cd into it:
`git clone https://github.com/blindchaser/gcp-hello-world.git`

Go to [Qwiklabs](https://ce.qwiklabs.com/), log in, and select Sabre GCP
Training and then the GCP Environment lab. Start the lab and accept all to the
pop-ups. Copy the Project ID and paste it into line 19 of the
`kubernetes/k8sconfig.yaml` file where it says YOUR_PROJECT_ID and also line 9
of the `src/main/resources/application.yaml` file.

In your terminal, after you've installed the Google Cloud SDK, run
`gcloud init` and log in with your Qwiklab credentials and select the qwiklab
project. You'll also want to run the below commands in your terminal.

```
USERNAME=$(gcloud config list account --format "value(core.account)")
PROJECT_ID=$(gcloud config get-value project)
PROJECT_NUMBER="$(gcloud projects describe ${PROJECT_ID} --format='get(projectNumber)')"
```

### Step 3: Virtual Machine Creation and Connection

First up, create a virtual machine via the UI by opening up the Navigation
Menu (the three horizontal lines in the upper left hand corner) and going to
`Compute Engine > VM instances > Create Instance`. The UI is pretty intuitive,
but the important fields are name, region & zone, machine configuration, and
boot disk. Name it gcp-demo-vm and leave everything else as default and create.
You can also create with the script below on your terminal.

```
gcloud compute instances create gcp-demo-vm \
  --image-family=rhel-8 \
  --image-project=rhel-cloud \
  --zone=us-central1-a
```

When it's created you can click the SSH button in the last column for your VM
or use the script below to connect from your machine.

```
gcloud compute ssh gcp-demo-vm
```

### Step 4: Spanner Database Creation

To create the Spanner Instance via UI: `Spanner > Create instance`.
Use the below values:
- name: `gcp-demo-spanner-instance`
- id: `gcp-demo-spanner-instance`
- Select `Regional` with a configuration of `us-central1`
- Node(s): `1`

Or

```
gcloud spanner instances create gcp-demo-spanner-instance \
  --config=regional-us-central1 \
  --description="gcp-demo-spanner-instance" \
  --nodes=1
```

To create the Spanner Database, simply select Create Database after your
instance is created. Name it `gcp-demo-db`. You'll see it has no tables/data,
but we'll be populating it using our code!

```
gcloud spanner databases create gcp-demo-db \
  --instance=gcp-demo-spanner-instance
```

Next, you'll need to add an IAM policy binding. In the right panel click
`Add Member`. Use the below values:
- member: your qwiklabs username
- role: Cloud Spanner > `Cloud Spanner Admin`

```
gcloud spanner databases add-iam-policy-binding gcp-demo-db \
  --instance="gcp-demo-spanner-instance" \
  --member="user:$USERNAME" \
  --role="roles/spanner.admin"
```

To create a key, go to IAM & Admin > Service Accounts. Click on the third one
(it should look like: YOUR_PROJECT_ID@YOUR_PROJECT_ID.iam.gserviceaccount.com)
click edit, and click create key at the bottom (JSON). It'll save to your
downloads so move it to the `src/main/resources` folder in your local
repository and rename it `credential.json`.

```
gcloud iam service-accounts keys create src/main/resources/credential.json \
  --iam-account "$PROJECT_ID@$PROJECT_ID.iam.gserviceaccount.com"
```

Finally, build, run, & ping our endpoint to populate database:

```
mvn clean install
java -jar target/gcp-demo-0.0.1-SNAPSHOT.jar
```

And open a new terminal window to run the curl command:

```
curl -v http://localhost:8080/update
```

To check that your data actually did get populated:
`Spanner > gcp_demo_spanner_instance > gcp_demo_db > cases_in_all_us_states > DATA`
(the tab).

### Step 5: Cloud Build

First, create your Cloud Source Repository and add it as a remote to your local
repo.

```
gcloud source repos create gcp-demo-repo
git remote add google https://source.developers.google.com/p/$PROJECT_ID/r/gcp-demo-repo
```

Then, create your GKE cluster: `Kubernetes Engine > Clusters > Create Cluster`
- name: `gcp-demo-cluster`
- zone: (make sure it's `Zonal`) `us-central1-a`

```
gcloud container clusters create gcp-demo-cluster \
  --num-nodes 1
  --zone us-central1-a
```

Next, add a new IAM policy binding:

```
gcloud projects add-iam-policy-binding ${PROJECT_NUMBER} \
  --member=serviceAccount:${PROJECT_NUMBER}@cloudbuild.gserviceaccount.com \
  --role=roles/container.developer
```

Create a Trigger:
`Cloud Build > Triggers > Create Trigger`:
- name: `gcp-demo-trigger`
- source: select `gco-demo-repo`
- branch: `.*`
- cloud build configuration file location: `cloudbuild.yaml`

```
gcloud beta builds triggers create cloud-source-repositories \
    --repo=gcp-demo-repo \
    --branch-pattern=".*" \
    --build-config=cloudbuild.yaml
```

Push to run:

```
git add . && git commit -m 'initial commit' && git push google
```

Go click on the history tab under Cloud Build and watch. You can also click on
your build's id/name to view the logs!

### Step 6: Understanding the Parts

Take a second to look trough the below files:
- `kubernetes/k8sconfig.yaml`
- `src/main/resources/application.yaml`
- `cloudbuild.yaml`

Notice where our above names are used in the config files and how it all ties
together. It might help to do a quick search for instances of `gcp-demo` in
this repository.

### Step 7: Clean Up

To clean up, end the lab and run the below:

```
git remote remove google
gcloud auth revoke $USERNAME
```

## References

* [gcloud Documentation](https://cloud.google.com/sdk/gcloud/reference)
