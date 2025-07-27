# CW-DARMM

Este proyecto implementa una versión en Java del modelo de manejo de riesgo **CW-DARMM** (Confidence-Weighted Dynamic Asymmetric Risk Management Model).

El sistema se compone de:

- Aplicación de escritorio construida con **Spring Boot** y **JavaFX**.
- Formularios y macros originales en VBA (archivos `.frm` y `.bas`) exportados desde el libro `RISK_MANAGER_MILLIONS_KELLY_noPassword.xlsm`.
- Documento de diseño "GOOD ARTICLE.pdf" donde se describe la filosofía y las fórmulas del modelo.

## Objetivo

Recrear la funcionalidad del Excel original en una aplicación multiplataforma manteniendo la lógica de negocio descrita en el artículo.

## Carpetas principales

- `src/main/java` – código Java de la aplicación.
- `src/main/resources` – archivos FXML, hojas de estilo y configuración.
- `docs/` – documentación de apoyo, formularios de VBA y el PDF del modelo.

## Cómo ejecutar las pruebas

```
mvn -q test
```

## Referencias

- **GOOD ARTICLE.pdf**: describe la derivación teórica completa del modelo. Algunas funciones como `calculateExpectancy` y `calculateRiskOfRuin` siguen directamente las fórmulas presentadas en el documento.
- **formularios .frm y módulos .bas**: contienen la versión original en VBA. Su estructura se ha mantenido para conservar la trazabilidad con el Excel.

