# Análisis de Seguridad — Jenkins
**Fecha:** 2026-02-28
**Entorno analizado:** `jenkins.ce.convene.com` (Producción)
**Entorno local (referencia):** Jenkins 2.541.2-lts-jdk17 (Docker)

---

## Contexto

Se realizó un análisis de seguridad sobre el entorno Jenkins de producción.
Los warnings fueron extraídos directamente desde:
**Manage Jenkins → Security → Security Warnings**

---

## Resumen

| Categoría | Cantidad de warnings |
|-----------|---------------------|
| Jenkins Core | 7 |
| Plugins | 14 |
| **Total** | **21** |

---

## Warnings de Jenkins Core

Producción muestra warnings de Jenkins Core porque corre una versión anterior a los fixes.

| Advisory | Versiones afectadas | Descripción |
|----------|--------------------|-|
| [2024-10-02](https://www.jenkins.io/security/advisory/2024-10-02/) | ≤ 2.478 weekly / ≤ 2.462.2 LTS | Multiple security vulnerabilities |
| [2024-11-27](https://www.jenkins.io/security/advisory/2024-11-27/) | — | Denial of service en bundled json-lib |
| [2025-03-05](https://www.jenkins.io/security/advisory/2025-03-05/) | ≤ 2.499 weekly / ≤ 2.492.1 LTS | Multiple security vulnerabilities |
| [2025-04-02](https://www.jenkins.io/security/advisory/2025-04-02/) | ≤ 2.503 weekly / ≤ 2.492.2 LTS | Multiple security vulnerabilities |
| [2025-09-17](https://www.jenkins.io/security/advisory/2025-09-17/) | ≤ 2.527 weekly / ≤ 2.516.2 LTS | Multiple security vulnerabilities |
| [2025-12-10](https://www.jenkins.io/security/advisory/2025-12-10/) | ≤ 2.540 weekly / ≤ 2.528.2 LTS | Multiple security vulnerabilities |
| [2026-02-18](https://www.jenkins.io/security/advisory/2026-02-18/) | ≤ 2.550 weekly / ≤ **2.541.1 LTS** | Multiple security vulnerabilities |

> **Nota:** El último advisory (2026-02-18) afecta hasta LTS 2.541.1.
> El entorno local ya corre **2.541.2 LTS** — todos estos warnings están resueltos.

---

## Warnings de Plugins

### Visible en producción (`jenkins.ce.convene.com`)

| Plugin | Advisory | SECURITY ID | Descripción | Versión fix |
|--------|----------|-------------|-------------|-------------|
| `script-security` | [2024-11-13](https://www.jenkins.io/security/advisory/2024-11-13/#SECURITY-3447) | SECURITY-3447 | Missing permission check | `1368.vb_b_402e3547e7` |
| `plain-credentials` | [2024-06-26](https://www.jenkins.io/security/advisory/2024-06-26/#SECURITY-2495) | SECURITY-2495 | Secret file credentials almacenadas sin encriptar | `183.va_de8f1dd5a_2b_` |
| `credentials` | [2024-10-02](https://www.jenkins.io/security/advisory/2024-10-02/#SECURITY-3373) | SECURITY-3373 | Encrypted values reveladas a usuarios con Extended Read | `1381.v2c3a_12074da_b_` |
| `workflow-cps` | [2024-11-13](https://www.jenkins.io/security/advisory/2024-11-13/#SECURITY-3362) | SECURITY-3362 | Rebuild con script approval revocado permitido | `3993.v3e20a_37282f8` |
| `docker-build-step` | [2024-03-06](https://www.jenkins.io/security/advisory/2024-03-06/#SECURITY-3200) | SECURITY-3200 | CSRF vulnerability + missing permission check | versión actual |
| `eddsa-api` | [2025-03-19](https://www.jenkins.io/security/advisory/2025-03-19/#SECURITY-3404) | SECURITY-3404 | EdDSA signature malleability | `0.3.0.1-16.vcb_4a_98a_3531c` |
| `folder-auth` | [2025-01-22](https://www.jenkins.io/security/advisory/2025-01-22/#SECURITY-3062) | SECURITY-3062 | Permisos deshabilitados pueden ser otorgados | versión actual |
| `htmlpublisher` | [2025-07-09](https://www.jenkins.io/security/advisory/2025-07-09/#SECURITY-3547) | SECURITY-3547 | File path information disclosure | `427` |
| `credentials-binding` | [2025-07-09](https://www.jenkins.io/security/advisory/2025-07-09/#SECURITY-3499) | SECURITY-3499 | Improper masking de credentials | `687.689.v1a_f775332fc9` |
| `opentelemetry` | [2025-09-03](https://www.jenkins.io/security/advisory/2025-09-03/#SECURITY-3602) | SECURITY-3602 | Missing permission check — captura credentials | `3.1543.1545.vf5a_4ec123769` |
| `jakarta-mail-api` | [2025-09-03](https://www.jenkins.io/security/advisory/2025-09-03/#SECURITY-3617) | SECURITY-3617 | SMTP command injection via email addresses | `2.1.3-3` |
| `git-client` | [2025-09-03](https://www.jenkins.io/security/advisory/2025-09-03/#SECURITY-3590) | SECURITY-3590 | File system information disclosure en agentes | `6.3.3` |
| `saml` | [2025-10-29](https://www.jenkins.io/security/advisory/2025-10-29/#SECURITY-3613) | SECURITY-3613 | Replay vulnerability — sin replay cache | `4.583.585.v22ccc1139f55` |
| `git-client` | [2025-12-10](https://www.jenkins.io/security/advisory/2025-12-10/#SECURITY-3614) | SECURITY-3614 | OS command injection en agentes | `6.4.1` |

---

## Dónde encontrar esto en Jenkins

**Ruta:** `Manage Jenkins → Security → Security Warnings`

En esa pantalla:
- Cada warning muestra el nombre del plugin/core afectado
- Tiene un link directo al advisory oficial en jenkins.io
- Se puede "suprimir" un warning (útil si se acepta el riesgo temporalmente)

**También visible en:** `Manage Jenkins → Plugins → Installed plugins`
- Los plugins con warning muestran un ícono ⚠️ junto al nombre
- Clic en ⚠️ → abre el advisory con el detalle del CVE

---

## Qué se hizo en el entorno local

### 1. Actualización de Jenkins Core
- **Antes (producción):** versión antigua (afectada por 7 advisories de Core)
- **Ahora (local):** `2.541.2-lts-jdk17` — resuelve todos los Core warnings listados

### 2. Sincronización y actualización de plugins
- Se exportó el listado de plugins desde producción vía Script Console
- Se actualizó `plugins.txt` con el mismo conjunto de plugins
- **Se quitaron los version pins** → jenkins-plugin-cli resuelve versiones compatibles con 2.541.2
- Motivo: las versiones de producción son incompatibles con Jenkins 2.541.2 (classloader issues con `commons-compress-api`, `docker-plugin`, etc.)
- **Resultado:** la mayoría de plugins se instalaron en versiones que ya incluyen los fixes

### 3. Plugins faltantes agregados
- `configuration-as-code` — faltaba en el listado original; sin él `casc.yaml` no se aplicaba y Jenkins arrancaba sin autenticación configurada

### 4. Bug corregido en script de inicialización
- **Archivo:** `init.groovy.d/load-pipeline.groovy`
- **Bug:** `jenkins.deleteItem(jobName)` lanzaba `MissingMethodException`
- **Fix:** `job.delete()` — el objeto `job` ya tenía la referencia al item

---

## Estado actual — Warnings en entorno local

El entorno local muestra actualmente **2 warnings de plugins**:

| Plugin | Warning | Motivo |
|--------|---------|--------|
| `docker-build-step` | CSRF vulnerability + missing permission check | Sin versión fix disponible aún |
| `folder-auth` | Disabled permissions can be granted | Sin versión fix disponible aún |

Los demás 12 warnings de plugins que muestra producción **no aparecen en local** porque los plugins se actualizaron a versiones que ya incluyen los fixes.

Los 7 warnings de Jenkins Core **no aparecen en local** porque actualizamos a 2.541.2 que los corrige.

---

## Acciones recomendadas para producción

| Prioridad | Acción | Detalle |
|-----------|--------|---------|
| 🔴 Alta | Actualizar Jenkins Core | Pasar de versión actual a 2.541.2 LTS |
| 🔴 Alta | Actualizar `saml` | CVE con CVSS 8.4 — replay vulnerability |
| 🟡 Media | Actualizar `git-client` | 2 CVEs: command injection + file system disclosure |
| 🟡 Media | Actualizar `opentelemetry` | Missing permission check — fuga de credentials |
| 🟡 Media | Actualizar `credentials-binding` | Credentials no enmascaradas en error logs |
| 🟡 Media | Actualizar `credentials` | Encrypted values expuestas |
| 🟡 Media | Actualizar demás plugins con warning | Ver tabla de "Versión fix" arriba |
| 🟠 Baja | Evaluar `docker-build-step` | Sin fix disponible aún — evaluar si es necesario |
| 🟠 Baja | Evaluar `folder-auth` | Sin fix disponible aún — evaluar si es necesario |

---

## Metodología

1. Se inició Jenkins local en Docker con mismo plugin set que producción
2. Se revisó `Manage Jenkins → Security → Security Warnings` en producción
3. Se consultaron los advisories oficiales en [jenkins.io/security/advisories](https://www.jenkins.io/security/advisories/)
4. Se documentaron versiones afectadas y versiones con fix disponible

---

*Análisis basado en datos de producción extraídos el 2026-02-28*
