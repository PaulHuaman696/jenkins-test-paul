# jenkins-shared-devops

Private repository to manage shared Jenkins configurations, Dockerfiles, and YAML deployment files used for CI/CD automation and collaborative DevOps workflows.

---

## ¿Qué hace este proyecto?

Levanta un Jenkins local usando Docker con configuración automática (JCasC), y lo conecta con GitHub Actions mediante ngrok para disparar pipelines automáticamente con cada push.

```
Push a GitHub
    → GitHub Actions se activa
        → llama a Jenkins via ngrok
            → Jenkins ejecuta el pipeline
```

---

## Requisitos previos

- [Docker](https://docs.docker.com/get-docker/) instalado
- [ngrok](https://ngrok.com/) instalado (`sudo snap install ngrok`)
- Cuenta gratuita en [ngrok.com](https://ngrok.com)
- Repositorio en GitHub

---

## Estructura del proyecto

```
jenkins-shared-devops/
├── .github/
│   └── workflows/
│       └── trigger-jenkins.yml   ← workflow de GitHub Actions
├── jenkins-docker/
│   ├── Dockerfile
│   ├── compose.yml
│   ├── casc.yaml                 ← configuración automática de Jenkins
│   ├── plugins.txt               ← plugins a instalar
│   └── .env                      ← variables de entorno
└── README.md
```

---

## Paso 1 — Levantar Jenkins local

```bash
cd jenkins-docker
docker compose up --build
```

Espera hasta ver en los logs:

```
Jenkins is fully up and running
```

Accede a Jenkins en: http://localhost:8080

| Campo | Valor |
|-------|-------|
| Usuario | `admin` |
| Contraseña | `admin` |

---

## Paso 2 — Exponer Jenkins con ngrok

En una terminal separada, configura tu authtoken (solo la primera vez):

```bash
ngrok config add-authtoken TU_TOKEN_AQUI
```

Obtén tu token en: https://dashboard.ngrok.com/get-started/your-authtoken

Luego expón Jenkins:

```bash
ngrok http 8080
```

Copia la URL pública que aparece, por ejemplo:

```
https://abc123.ngrok-free.dev
```

> ⚠️ Esta URL cambia cada vez que reinicias ngrok. Deberás actualizar el secret `JENKINS_URL` en GitHub si la reinicias.

---

## Paso 3 — Obtener API Token de Jenkins

1. Entra a http://localhost:8080
2. Click en **admin** (arriba a la derecha) → **Security**
3. Sección **Clave del API (Token)** → **Add new Token**
4. Nombre: `github-actions` → **Generate**
5. **Copia el token** (solo se muestra una vez)

---

## Paso 4 — Configurar Secrets en GitHub

En tu repositorio en GitHub → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**:

| Name | Secret |
|------|--------|
| `JENKINS_URL` | `https://abc123.ngrok-free.dev` |
| `JENKINS_TOKEN` | el token generado en el paso anterior |

---

## Paso 5 — Crear el job en Jenkins

1. En Jenkins UI → **Nueva Tarea**
2. Nombre: `test-pipeline`
3. Tipo: **Pipeline** → **OK**
4. En la sección **Pipeline Script** pega:

```groovy
pipeline {
    agent any
    stages {
        stage('Hello') {
            steps {
                echo 'Hello desde GitHub Actions!'
            }
        }
    }
}
```

5. Click **Save**

---

## Paso 6 — Verificar el flujo completo

Haz cualquier commit y push a la rama `main`:

```bash
git add .
git commit -m "test: trigger jenkins"
git push
```

Luego verifica:

- **GitHub** → pestaña **Actions** → el workflow debe aparecer verde ✅
- **Jenkins** → el job `test-pipeline` debe mostrar una ejecución exitosa ✅

---

## Comandos útiles

```bash
# Levantar Jenkins en background
docker compose up -d

# Ver logs de Jenkins
docker compose logs -f

# Detener Jenkins (datos persisten)
docker compose down

# Detener Jenkins y borrar todo
docker compose down -v

# Reconstruir si cambias Dockerfile o plugins
docker compose up --build
```

---

## Notas

- Jenkins y ngrok deben estar corriendo al mismo tiempo para que el workflow funcione.
- La URL de ngrok en el plan gratuito cambia cada vez que reinicias el túnel. Para una URL fija considera un plan pago de ngrok o desplegar Jenkins en AWS/VPS.
- Las credenciales `admin/admin` son solo para desarrollo local. Nunca las uses en producción.