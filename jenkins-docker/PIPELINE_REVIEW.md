# Pipeline Review - Jenkins Plugins Testing

## Contexto del Proyecto

El objetivo era crear un pipeline de Jenkins que probara diversos plugins instalados, verificando que funcionen correctamente en conjunto. Los plugins a testear están definidos en `plugins.txt`:

- **Core/Config:** configuration-as-code
- **Pipelines:** workflow-aggregator, pipeline-stage-view, job-dsl
- **Git:** git-client, git
- **Docker:** docker-workflow
- **UI/UX:** blueocean, timestamper, ansicolor
- **Credentials:** credentials

## Estructura del Pipeline

El `Jenkinsfile` contiene las siguientes etapas:

| Stage | Plugin(s) a probar | Descripción |
|-------|-------------------|-------------|
| Git Checkout | git | Clona un repositorio Git |
| Create Mock App | - (sh) | Crea archivos necesarios para el build |
| Docker Build | docker-workflow | Build de imagen Docker |
| Docker Run & Test | docker-workflow | Ejecuta y verifica el contenedor |
| Job DSL - Crear Job Dinamico | job-dsl | Crea un job dinámicamente via DSL |
| Credentials Test | credentials | Prueba uso de credenciales |
| BlueOcean/UI Features | blueocean, ansicolor, timestamper | Verifica features de UI |
| Pipeline Stage View | pipeline-stage-view | Verifica visualización de stages |

---

## Problemas Encontrados y Soluciones

### 1. Error con plugin Docker (`docker.run()`)

**Error:**
```
Scripts not permitted to use method groovy.lang.GroovyObject invokeMethod 
java.lang.String java.lang.Object (org.jenkinsci.plugins.docker.workflow.Docker run ...)
```

**Causa:** El Script Security de Jenkins bloquea la llamada al método `docker.run()` del plugin.

**Solución:** Reemplazar las llamadas del plugin por comandos `sh` directos:

```groovy
// ANTES (plugin)
docker.build("imagen:tag")
docker.run("imagen:tag", "sleep 5")

// DESPUÉS (sh)
sh "docker build -t imagen:tag ."
sh "docker run -d --name contenedor imagen:tag"
```

**Ventaja:** No requiere aprobación de scripts, menos dependencia de versiones de plugins.

---

### 2. Puerto 8080 ocupado

**Error:**
```
Bind for 0.0.0.0:8080 failed: port is already allocated
```

**Solución:** Cambiar a puerto 8888 y agregar limpieza previa:

```groovy
sh """
    docker rm -f test-container-\${BUILD_NUMBER} 2>/dev/null || true
    docker run -d --name test-container-\${BUILD_NUMBER} -p 8888:80 imagen:tag
"""
```

---

### 3. Error con Job DSL (`job()` no existe)

**Error:**
```
java.lang.NoSuchMethodError: No such DSL method 'job' found among steps
```

**Causa:** El método `job()` no está disponible directamente en un pipeline declarativo. Se debe usar el step `jobDsl`.

**Solución:** Usar `jobDsl` con `scriptText`:

```groovy
// ANTES
job("test-job") { ... }

// DESPUÉS
jobDsl scriptText: '''
    job("test-job") {
        ...
    }
'''
```

---

### 4. Error con Job DSL - Script no aprobado

**Error:**
```
ERROR: script not yet approved for use
```

**Causa:** El script generado por Job DSL requiere aprobación en Script Security.

**Intento de solución:** Intentar agregar approvals en JCasC (`casc.yaml`):

```yaml
unclassified:
  scriptApproval:
    approvedSignatures:
      - "method hudson.model.Job createClone java.lang.String"
```

**Resultado:** Error al iniciar Jenkins - `scriptApproval` no es válido en JCasC en esta versión.

---

## In-Process Script Approval

### ¿Qué es?

Es una funcionalidad de seguridad de Jenkins que requiere aprobación explícita para ejecutar scripts Groovy o métodos específicos dentro de pipelines.

### Cómo aprobar manualmente (UI)

1. Ir a **Manage Jenkins > In-process Script Approval**
2. Buscar el método/script bloqueado
3. Click en "Approve"

### Intentamos resolverlo por código

Se intentó agregar approvals al archivo `casc.yaml` pero falló:

```
UnknownAttributesException: Invalid configuration elements for type: ... : scriptApproval
```

Esto ocurre porque el plugin `configuration-as-code` no soporta la configuración de `scriptApproval` en la versión actual.

