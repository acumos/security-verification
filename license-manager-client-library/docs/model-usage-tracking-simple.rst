.. ===============LICENSE_START================================================
.. Acumos CC-BY-4.0
.. ============================================================================
.. Copyright (C) 2019 Nordix Foundation
.. ============================================================================
.. This Acumos documentation file is distributed by Nordix Foundation.
.. under the Creative Commons Attribution 4.0 International License
.. (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
..      http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END==================================================
..


===================================
Model Usage Tracking - Simple Model
===================================


Simple Model deployment (Kubernetes cluster)
--------------------------------------------

In this section, we will refer to simple model "face-privacy-filter-detect".

Deploying model
^^^^^^^^^^^^^^^

* Once a Simple AI model onboarded to Acumos platform, you can download
  the solution using the "Deploy to Local" feature.

  .. image:: images/downloadSimpleSol-1.png

  Click the "Download Solution Package" button to download the model solution
  package for deployment.

  .. image:: images/downloadSimpleSol-2.png

  You will notice following artifacts from the downloaded solution package.

  .. code-block:: console

    ls -al
    total 36
    drwxrwxr-x  2 acumos acumos  4096 Jun  4 00:41 .
    drwxrwxr-x 12 acumos acumos  4096 Jun  4 00:41 ..
    -rw-rw-r--  1 acumos acumos 15992 Jun  4 00:24 deploy.sh
    -rw-rw-r--  1 acumos acumos   258 Jun  4 00:24 deploy_env.sh
    -rw-rw-r--  1 acumos acumos  3291 Jun  4 00:24 setup_k8s.sh
    -rw-rw-r--  1 acumos acumos   857 Jun  4 00:24 solution.yaml

* Deploy the model solution to your Kubernetes cluster using
  following command:

  .. code-block:: console

    bash deploy.sh <user> <pass> <namespace> [datasource]
      user: username on the Acumos platform
      pass: password on the Acumos platform
      namespace: Kubernetes namespace to deploy the solution under
      datasource: (optional) file path or URL of data source for databroker

  For example, deploying the model solution under the
  namespace "cmp6" would result in following running services:

  .. code-block:: console

    kubectl get pods,svc -n cmp6
    NAME                                                         READY   STATUS    RESTARTS   AGE
    pod/face-privacy-filter-detect-5bdb9c77f7-nnk5s              1/1     Running   0          11m
    pod/filebeat-t6v56                                           1/1     Running   0          11m
    pod/nginx-proxy-face-privacy-filter-detect-9b5645598-6drbh   1/1     Running   0          11m

    NAME                                             TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
    service/face-privacy-filter-detect               ClusterIP   10.107.67.206   <none>        8556/TCP         11m
    service/nginx-proxy-face-privacy-filter-detect   NodePort    10.107.115.48   <none>        8550:30550/TCP   11m

  NOTE: To enable model usage tracking, there is a new nginx-proxy service running on port 30550.


Running model
^^^^^^^^^^^^^
* With Boreas release, the AI model can be accessed (via model runner)
  using following URL pattern:

  .. code-block:: html

    http://<model-runner-host>:<nginx-model-svc-port>/model/methods/<methodName>

  You can get method name from the protobuf rpc section.

  For the example model "face-privacy-filter-detect", it would be

  .. code-block:: html

    http://<model-runner-host>:30550/model/methods/detect

  NOTE:
    - The nginx reverse proxy for simple model is exposed on port 30550.
    - The new model runner takes protobuf as accept and content-type
      i.e. Accept: application/vnd.google.protobuf and Content-Type: application/vnd.google.protobuf

  .. image:: images/runningSimpleSol.png



Access model usage logs in Kibana
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The model usage tracking data are submitted to Acumos platform
Elastic Stack. The Acumos platform Elastic Search service stores model
usage data with the index name "acumos-model-usage-logs".

* Admin can login to Kibana Dashboard and create index pattern
  "acumos-model-usage-logs" with Time Filter field "@timestamp".

  .. image:: images/createKibanaIndex.png

* After adding the "acumos-model-usage-logs" index pattern, switch to the
  Discover tab to search the model usage records at different time intervals.

* As an Admin, you can observe the requested model usage data along with
  request/response details logged by nginx reverse proxy service.

  .. image:: images/simpleModelKibana-1.png

* Switch to the JSON tab of any record to review the raw json data of
  model usage record.

  .. image:: images/simpleModelKibana-2.png

* Admin can create Visual Objects based on different visualization types.

* In this Guide, we will create Visualization to show usage metrics of
  face-privacy-filter detect model.

  .. image:: images/visualize-Kibana-1.png

* To create Metrics visualization,

  1. Select "acumos-model-usage-logs" as search source.
  2. Under Buckets section, click "Split Group".
  3. Under Aggregation dropdown, select "Filter" aggregation type.

    .. image:: images/visualize-Kibana-2.png

  4. For Filter aggregation, add filter query based on model metadata, for example,

     .. code-block:: console

        model.solutionId:<<model_solution_id>>

    You can also set the Filter label for visual clues.

    NOTE:
    You can use any other model fields to filter usage data based on
    specific requirements.
    For example, you can use model.revisionId to filter
    usage records for a specific version of model.

  5. Click > button to apply/save changes and see the count change for each
     model method request.

     NOTE: You can turn on "Auto Refresh" to update UI for any model usage
     data at specific time intervals.

    .. image:: images/simple-model-usage-1.gif

