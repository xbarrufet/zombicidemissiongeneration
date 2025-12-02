## Consideraciones
Preguntame las dudas antes de hacer cualquier accion
Ves paso a paso dando feedback, espera confirmacion y/o comentarios para seguir


## fase 1
En el fichero ZoneMissionDraw quiero que cuando mientras inSelectedTokenState() se pinte un TokenPlaceHolder centrado en el cursor y que vaya siguiendo los movimientos del mouse has hacer click derecho o pulsar la tecla ESC

- inSelectedTokeState ya existe, se pone a true cuando se ha lanzado el evento desde ZoneMissionPropoerties de seleccion de token
- Click derecho llama a la funcion placeToken(x, y) de momento vacia
- ESC pone selectedToken a lase
- El placeHolder se crea con el TokenShape de Token, el topvertex getTopLeftFromCenter(cursor.x, cursor.y)




