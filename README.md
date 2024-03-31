 # QRStockMate: Sistema de Gestión Logística Multiplataforma
<h1 align="center"><img height="80" align="center" src="./AssetsDoc/app_icon_removed.png"/></h1>


<p align="center"> 
<img src="https://img.shields.io/badge/Plataforma-Web|Android-green"/> <img src="https://img.shields.io/badge/Framework-Angular|.NET|SDK Android|JetCompose|Swagger-red"/> 
<img src="https://img.shields.io/badge/Angular-^15.2.2-green"/> 
<img src="https://img.shields.io/badge/Versi%C3%B3n-3.2.7-gray"/> 
<img src="https://img.shields.io/badge/Language-Typescript|Javascript|HTML|CSS|kotlin-lightblue"/>
<img src="https://img.shields.io/badge/Backend-Firebase|SqlServer|ASP.NET|Swagger|WebHook-orange"/>
</p>

<p>QRStockMate es un proyecto que.</p> 

Dashboard            |  Transportation Tracking
:-------------------------:|:-------------------------:
![](https://github.com/AcoranGonzalezMoray/TFT-SistemaGestionLogisticaMultiplataforma/blob/main/AssetsDoc/Captura%20de%20pantalla%202024-03-29%20215723.png)  |  ![](./AssetsDoc/Screenshot_20240329_213652.png)

 
## Contenido
<div>

  <h3>1.Presentación</h3>
  <h3>2.Funciones</h3>
  <h3>3.Ejecución</h3>
  <h3>4.Tecnologías</h3>
 
</div>

## Presentación
En el entorno digital actual, las aplicaciones logísticas desempeñan un papel crucial en la eficiencia y sostenibilidad de las empresas. La digitalización, especialmente a través de plataformas web y móviles, no solo optimiza los procesos empresariales, sino que también contribuye significativamente al desarrollo sostenible. Al minimizar la necesidad de recursos físicos y optimizar las rutas de entrega, estas aplicaciones reducen la huella de carbono y promueven prácticas empresariales eco-amigables.

Este Trabajo Fin de Grado propone el desarrollo de una aplicación logística avanzada, diseñada tanto para web como para dispositivos móviles Android. El objetivo principal es crear un sistema de gestión integral que facilite las operaciones logísticas, mejorando la eficiencia, la precisión y la capacidad de respuesta de las empresas frente a las demandas del mercado.

## Funciones
El sistema QRStockMate tiene las siguientes funcionalidades entre otras:
##### Cliente Android / Web
<li>Seguimiento de transportes.</li>
<li>Funcionalidades basadas en roles.</li>
<li>Chat completo con funcionalidad de audio llamadas y envío de archivos en vivo.</li>
<li>Registro de almacenes, artículos, vehículos, etc.</li>
<li>Gestión de empleados de la empresa de logística.</li>
<li>Versión web para monitorizar todos esos componentes.</li>
<li>Dashboard totalmente personalizable con gráficas en movimiento y widgets u otro tipo de componentes.</li>
<br>

Android Screen (not all)          |
:-------------------------:|
![](https://github.com/AcoranGonzalezMoray/TFT-SistemaGestionLogisticaMultiplataforma/blob/main/AssetsDoc/image%20(4).png)  |  


 Web Tracking            |  App Tracking
:-------------------------:|:-------------------------:
![](https://github.com/AcoranGonzalezMoray/TFT-SistemaGestionLogisticaMultiplataforma/blob/main/AssetsDoc/Captura%20de%20pantalla%202024-03-29%20214656.png)  |  ![](https://github.com/AcoranGonzalezMoray/TFT-SistemaGestionLogisticaMultiplataforma/blob/main/AssetsDoc/2024-03-29-21-35-37.gif)


#### API
<li>.NET Health check</li>
<li>Swagger Doc</li>
<li>.NET WebHooks</li>
<br>

Health check           |  WebHooks | Swagger
:-------------------------:|:-------------------------:|:-------------------------:
![]()  |  ![]() |  ![]()



## Ejecución
Para ejecutar el proyecto, sigue los siguientes pasos:
### Aplicación Web:
1. Primero accede a QRStockMateWeb
2. Luego procede con las siguientes acciones:
 ```
 // descarga las dependencias
 npm i 

 //Crea un archivo en /QRStockMateWeb/src/environment/keys.ts, con el siguiente contenido:
 export const key = {
     MAP: "AQUI VA TU APIKEY PARA MAPTILTER"
 }

 // Ejecuta la aplicacion web
 ng serve -o 
 ```
### Aplicación Android:
1. Primero accede a QRStockMateApp
2. Luego procede con las siguientes acciones:
 ```
 //Crea un archivo en /QRStockMateApp/local.properties, con el siguiente contenido:
 MAPS_API_KEY="AQUI VA TU API PARA GOOGLE MAP DE GOOGLE CLOUD"
 ```

### Aplicación API:
1. Primero accede a QRStockMateSL
2. Luego procede con las siguientes acciones:
 ```
 //Crea un archivo en /QRStockMateSL/QRStockMate.AplicationCore/Entities/keySMPT.cs, con el siguiente contenido:
	public static class Key {
			public static readonly string ApiKeyFirebase = "AQUI VA TU API DE FIREBASE";
			public static readonly string ApiKey= "AQUI VA TU CONTRASEÑA DE APLICACION DE UNA CUENTA GOOGLE PARA LA NOTIFICACION POR CORREO";
	}
 ```
## Tecnologías Y Arquitecturas
### Backend
 - Arquitectura: Arquitectura Limpia
 - API: ASP.NET y Visual Studio (C#)
 - BD Relacional: SqlServer
 - BD No Relacional: Firebase Storage
 - Seguridad del Sistema: JsonWebToken
 - .NET Health check: HealthCheck UI
 - .NET WebHooks: HealthCheck UI - WebHooks
 - DOC: Swagger

### Frontend/Clientes
 - Aplicación Móvil: Android Studio (Kotlin, JetCompose, SDK), Google Map
 - Aplicación Web: Framework Angular (Typescript), Map Tilter, Gmail


## Objetivos Personales
Adquisición de experiencia en el establecimiento de criterios para la toma de decisiones en el ciclo de desarrollo completo de una aplicación software.
Desarrollo de un proyecto totalmente exportable a otras plataformas.
Mayor comprensión del ecosistema de Android Studio, Visual Studio y los Frameworks empleados.
Este README proporciona una visión general del proyecto QRStockMate, detallando su propósito, funcionalidades, tecnologías utilizadas y cómo ejecutarlo.
