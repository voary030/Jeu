
@echo off
rem Script pour lancer le serveur et le client dans 2 fenêtres cmd séparées

rem Se placer dans le répertoire du script (racine du projet)
pushd "%~dp0"

rem Fenêtre 1 : Serveur
start "Serveur Jeu" cmd /k "mvn -q -Dexec.mainClass=com.pong.server.ServerMain exec:java -Dexec.args=5555"

rem Fenêtre 2 : Client (application JavaFX)
start "Client Jeu" cmd /k "mvn -q -Dexec.mainClass=com.pong.Main exec:java"

rem Revenir au dossier précédent
popd

echo Lancement demandé. Deux fenêtres cmd ont été ouvertes.
