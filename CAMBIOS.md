# Cambios realizados

## Funcionalidad 1: umbral de alarma

Se incorporó la posibilidad de configurar el umbral de temperatura desde la interfaz de la aplicación.

El valor ingresado se valida en `PlantViewModel`, aceptando únicamente valores entre 30 °C y 120 °C. Si el valor es válido, se guarda mediante `PlantRepository` en la tabla `configuration` de SQLite, utilizando `PlantDatabase`.

Al iniciar la aplicación, se recupera el último umbral guardado. La pantalla actualiza el valor configurado y recalcula inmediatamente la cantidad de equipos en alerta. Se considera en alerta un equipo cuya temperatura sea mayor o igual al umbral establecido.

## Funcionalidad 2: historial

Se incorporó la consulta del historial de lecturas de cada equipo.

La consulta se realiza desde `PlantDatabase`, recuperando las últimas 10 lecturas correspondientes al identificador del equipo, ordenadas desde la más reciente hasta la más antigua.

`PlantRepository` proporciona el acceso a esos datos y `PlantViewModel` administra el equipo seleccionado y las lecturas obtenidas. Finalmente, `DashboardScreen` muestra el historial en una ventana con fecha y hora, temperatura y origen de cada lectura.

También se contempla el caso de equipos sin historial y la posibilidad de cerrar la ventana y regresar al listado.

## Dos refactorizaciones

- **Problema 1:** `findLatestByName()` incorporaba directamente el texto del filtro dentro de la consulta SQL. **Corrección:** se reemplazó la interpolación por parámetros mediante `?` y `selectionArgs` en `rawQuery()`. **Beneficio:** se evita que el contenido ingresado se interprete como parte de la instrucción SQL, reduciendo el riesgo de inyección SQL y errores de búsqueda.

- **Problema 2:** `onUpgrade()` eliminaba las tablas `readings` y `configuration` y las creaba nuevamente. **Corrección:** se eliminó el borrado automático de las tablas y se dejó preparado el método para implementar migraciones que preserven los datos. **Beneficio:** se evita la pérdida automática de lecturas y configuraciones al actualizar la versión de la base de datos.

## Evidencia de validación

- Se compiló el proyecto mediante Android Studio utilizando Build > Make Project.
- Se debe registrar la prueba de configuración del umbral, verificando la aceptación de valores entre 30 °C y 120 °C y el rechazo de valores fuera del rango.
- Se debe registrar que el umbral configurado permanece guardado después de cerrar y volver a abrir la aplicación.
- Se debe comprobar que el contador de alertas se actualiza al modificar el umbral.
- Se debe comprobar que el historial muestra las últimas 10 lecturas de cada equipo en orden descendente, incluyendo fecha y hora, temperatura y origen.
- Se debe comprobar que el filtro de equipos se conserva al cerrar el historial.
- Se debe comprobar que una nueva sincronización incorpora lecturas al historial y que la consulta del historial sigue funcionando sin conexión a Internet.