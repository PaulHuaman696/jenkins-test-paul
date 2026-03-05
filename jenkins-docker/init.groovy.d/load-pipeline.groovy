import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

def jenkins = Jenkins.get()
def jobName = "test-pipeline-v2"
def jenkinsfilePath = "/var/jenkins_home/jenkinsfile/Jenkinsfile"

println "=== Init Script: Configurando job '${jobName}' ==="

def jenkinsfile = new File(jenkinsfilePath)
if (!jenkinsfile.exists()) {
    println "ERROR: Jenkinsfile no encontrado en ${jenkinsfilePath}"
    return
}

def jenkinsfileContent = jenkinsfile.text
def flowDefinition = new CpsFlowDefinition(jenkinsfileContent, true)

def job = jenkins.getItem(jobName)

if (job != null) {
    println "Job '${jobName}' existe. Eliminando para recrear..."
    job.delete()
}

def newJob = new WorkflowJob(jenkins, jobName)
newJob.setDefinition(flowDefinition)
newJob.setDescription("Pipeline de prueba configurado automáticamente")

jenkins.add(newJob, jobName)
jenkins.save()

println "Job '${jobName}' creado exitosamente!"
println "=== Init Script completado ==="
