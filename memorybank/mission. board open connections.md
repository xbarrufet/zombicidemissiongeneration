### Implementatcion de la creacion de las edge connections en la creacion del mission board

Se debe implementar la creacion de las edge connections en la creacion del mission board una vez se han unido todas los tiles y las conexiones no edge

## Contexto
Cada edge conneciton tiene una area A y una direccion. La conexion resultante sera la union de la areaa A con el Area situada en la direccion indicada
Ex: Area A tiene la direccion NORTH_LEFT, por lo que la union sera la union de Area A con el Area situada en la direccion  NORT_LEFT

## Casuistoca 1
Area en la direccion es una STREET_LOCATION, en este caso se podra usar la STREET_LOCATION como Area B y si situacion Ex: NORTH_LEFT --> se unira con el Area BOTTOM_LEFT del area situada al Norte de la Area A

## Casuistoca 2
El area en la direccion es una INDOOR LOCATION, en este caso debe haber una edge connection de la categorioa contraria en el area B. EX Area A NORTH_LEFT --> Area B SOUTH_LEFT
