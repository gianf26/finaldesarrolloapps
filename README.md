## Escenario

El equipo de software de una planta de fabricación necesita evolucionar un panel Android de monitoreo IIoT. Recibís una aplicación ya iniciada que ejecuta MVVM, se conecta a una API pública con Retrofit, conserva lecturas con SQLite, obtiene sensores del teléfono y mezcla componentes XML con Jetpack Compose.

Tu trabajo es continuar la base existente, **manteniendo la app operativa**. No reemplaces el proyecto entero por una solución nueva. Los equipos de la pantalla son simulados; la API se usa con fines académicos.

## Actividades obligatorias

### 1. Umbral de alarma configurable y persistente (30 puntos)

Actualmente el botón de umbral solo informa que la opción está pendiente. Implementá una interacción en Compose que permita definir un **umbral de temperatura en °C**:

- Aceptá únicamente valores numéricos dentro de **30 a 120 °C** y mostrá un mensaje claro si el valor no es válido.
- Guardá el umbral de manera persistente en **SQLite**, de modo que sobreviva al cierre y nueva apertura de la app.
- Al guardarlo, actualizá **de inmediato** los estados NORMAL/ALERTA y el contador de alertas de la pantalla, sin necesitar sincronizar.
- El valor inicial, en una instalación nueva, es **65 °C**. Una lectura alcanza el estado ALERTA cuando su temperatura es **mayor o igual** al umbral.

### 2. Historial de telemetría por equipo (30 puntos)

Actualmente **Ver historial** solo muestra un aviso. Implementá la consulta y presentación de un historial:

- Al pulsar en un equipo, mostrá sus **últimas 10 lecturas** guardadas en SQLite, ordenadas desde la más reciente.
- Cada registro debe mostrar al menos **fecha/hora, temperatura y origen**.
- Permití cerrar el historial y volver al panel sin perder la búsqueda actual.
- Si no existen registros, mostrá un estado vacío comprensible.
- Después de sincronizar dos veces, las nuevas lecturas deben aparecer en el historial del equipo.

### 3. Refactorización y justificación (25 puntos)

Identificá y mejorá **al menos dos problemas reales** que encuentres en el diseño, responsabilidades, seguridad, testabilidad o rendimiento. No alcanza con cambiar nombres, formato o colores. Preservá las funcionalidades ya existentes.

Incluí una explicación breve de cada decisión en `CAMBIOS.md`: qué problema encontraste, qué código modificaste y qué riesgo o costo resolvés.

### 4. Validación y entrega (15 puntos)

- El proyecto debe compilar y ejecutar, sin errores al navegar o sincronizar sin red.
- Debe conservar Retrofit, SQLite, el uso de sensores y la convivencia XML/Compose.
- Agregá al menos **una evidencia reproducible** de validación: prueba automatizada, prueba manual con pasos y resultado, o ambas.
- Entregá el proyecto y un archivo `CAMBIOS.md`. Se valorará código legible y coherente, no la cantidad de líneas.

## Condiciones del examen

- Se permite IA generativa, buscadores y documentación; sos responsable de verificar, adaptar y comprender lo que entregás.
- No se requiere acceso a sensores industriales, credenciales ni hardware adicional; un emulador es suficiente.
- No se califica el aspecto visual salvo que impida completar la funcionalidad.
- No es necesario implementar login, notificaciones push, MQTT ni gráficos avanzados.
