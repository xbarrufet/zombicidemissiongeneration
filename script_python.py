import os

def generate_file_strings(root_path):
    """
    Lee las subcarpetas y ficheros dentro de un path dado y genera un string
    con un formato específico para cada fichero.

    Args:
        root_path (str): El path base desde donde comenzar la lectura.

    Returns:
        list: Una lista de strings generados.
    """
    output_strings = []

    # 1. Recorrer las subcarpetas dentro del path raíz
    for subdir_name in os.listdir(root_path):
        subdir_path = os.path.join(root_path, subdir_name)

        # Asegurarse de que el elemento es un directorio
        if os.path.isdir(subdir_path):
            # 2. Generar el componente de la subcarpeta (singularizado y formateado)
            # Simplificación: Asume que eliminar la 's' al final hace singular.
            # En un caso real, se podría usar una librería de singularización
            # o un diccionario de mapeo.
            
            # Formatear el nombre de la subcarpeta:
            # - Quitar 's' final si existe (singularización simple).
            # - Pasar a minúsculas.
            # - Reemplazar espacios y guiones con '_'.
            
            singular_subdir = subdir_name.rstrip('s')
            
            # Función para normalizar el nombre
            def normalize_name(name):
                #poner el nombre en camelcase
                name = name.title()
                return normalized.strip('_')

            formatted_subdir = singular_subdir

            # 3. Recorrer los ficheros dentro de la subcarpeta
            for file_name in os.listdir(subdir_path):
                file_path = os.path.join(subdir_path, file_name)

                # Asegurarse de que el elemento es un fichero
                if os.path.isfile(file_path):
                    # 4. Generar el componente del fichero (formateado)
                    
                    # Eliminar la extensión antes de normalizar
                    base_file_name, _ = os.path.splitext(file_name)

                    # 5. Construir el string final
                    output_string = (
                        f"image.token.{formatted_subdir}.{base_file_name} = {file_path}"
                    )
                    output_strings.append(output_string)

    return output_strings

# --- Ejemplo de Uso ---
# NOTA: Reemplaza '/ruta/a/tu/carpeta/base' con el path real.
# Para el ejemplo, usaremos el directorio actual ('.').
# ¡Asegúrate de que este path es válido!

root_directory = './src/main/resources/images/tokens' # <-- CAMBIA ESTO AL PATH DESEADO
# root_directory = 'C:/Users/Usuario/Mis_Imágenes' 

# --- Creación de Directorios y Ficheros de Prueba (Opcional, para demostrar) ---
# Si quieres probarlo, descomenta el bloque de abajo para crear una estructura:
"""
try:
    os.makedirs(root_directory, exist_ok=True)
    os.makedirs(os.path.join(root_directory, 'Animales Salvajes'), exist_ok=True)
    os.makedirs(os.path.join(root_directory, 'Coches_deportivos'), exist_ok=True)
    
    with open(os.path.join(root_directory, 'Animales Salvajes', 'Leon Africano.jpg'), 'w') as f: f.write('')
    with open(os.path.join(root_directory, 'Animales Salvajes', 'Elefante-asiatico.png'), 'w') as f: f.write('')
    with open(os.path.join(root_directory, 'Coches_deportivos', 'Ferrari-F40.bmp'), 'w') as f: f.write('')

    print(f"Estructura de prueba creada en: {root_directory}")
except Exception as e:
    print(f"No se pudo crear la estructura de prueba: {e}")
"""
# ---------------------------------------------------------------------------------

try:
    if os.path.isdir(root_directory):
        generated_strings = generate_file_strings(root_directory)

        ## Muestra los resultados
        print("\n--- Strings Generados ---")
        for s in generated_strings:
            print(s)
        print("---------------------------\n")
        
    else:
        print(f"Error: El path '{root_directory}' no es un directorio válido o no existe.")

except Exception as e:
    print(f"Ocurrió un error al procesar el path: {e}")