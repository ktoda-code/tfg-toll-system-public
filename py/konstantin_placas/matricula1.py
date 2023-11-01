import cv2 # funciones para imagenes, la libreria para vision por computador
import imutils # para imagenres, mas basica, o las dos 
import numpy as np
import pytesseract # libreria de opencv para ocr (algoritmo) buscar caracteres sobre una imagen

pytesseract.pytesseract.tesseract_cmd = r'c:\Program Files\Tesseract-OCR\tesseract.exe'


for i in range(1,2):
    file=""
    # file=file+(str(i))+".jpg"
    img = cv2.imread('1.jpg',cv2.IMREAD_COLOR)
    # img = cv2.imread(file,cv2.IMREAD_COLOR) # convertir RGB color
    img = cv2.resize(img, (800,600) ) # reducir tamanio de la imagen , funciona mejor sin rescalar la imagen
    
    # Metodo viejo, mejor metodo -> red neuronal

    #proceso: https://programarfacil.com/blog/vision-artificial/detector-de-bordes-canny-opencv/
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)#HSV ... hsv mejor pero es mas dificl
    cv2.imshow('Gray',gray) # siempre a gris (leer porque)
    #difuninar la imagen (https://pyimagesearch.com/2021/04/28/opencv-smoothing-and-blurring/): quitar ruido y reducir bordes
        #Simple blurring (cv2.blur)
        #Weighted Gaussian blurring (cv2.GaussianBlur)
        #Median filtering (cv2.medianBlur)
        #Bilateral blurring (cv2.bilateralFilter)
    blur = cv2.GaussianBlur(gray, (5,5), 0) #param 3: desviación estándar en la dirección del eje X. Poniendo 0, lo calcula automaticamente!
    
    cv2.imshow('Blur',blur)
    #blur = cv2.bilateralFilter(gray, 13, 15, 15) 
    #cv2.imshow('Blur',blur)
    #cv2.waitKey(0)

    # Bordes: https://programarfacil.com/blog/vision-artificial/detector-de-bordes-canny-opencv/
    #buscar cambios entre los píxeles del alrededor donde ese cambio está dentro de un umbral: 100-150
    edged = cv2.Canny(blur, 100, 150)# 30-200 
    cv2.imshow('Edged',edged)
    #cv2.waitKey(0)
    #contours = cv2.findContours(edged.copy(), cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    contours =cv2.findContours(edged.copy(),cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
    contours = imutils.grab_contours(contours)# no tengo muy claro para qué es! en estepunto me daba error antes de hacer sort y busqué en internet!
    contours = sorted(contours, key = cv2.contourArea, reverse = True)[:40]# ordenar del área mas grande a más pequeña
    screenCnt = None
    print("Numero de contornos encontrados: " + str(len(contours)))
    first=1
    for c in contours:
        # Contar el numero de los pixeles y sacar la media el color de los pixeles y sacar cuantos pixeles salen
        peri = cv2.arcLength(c, True)
        approx = cv2.approxPolyDP(c, 0.018 * peri, True)
        cv2.drawContours(img, [approx], -1, (0, 255, 0), 3)
        #cv2.imshow('draw_contour',img)
        temp = np.zeros_like(gray)
        cv2.drawContours(temp, [approx], 0, (255,255,255), -1)
        #mask = np.zeros(img, np.uint8)
        #cv2.drawContours(mask, c, -1, 255, -1)
        mean = cv2.mean(img, temp)
        kont = cv2.contourArea(approx)
        #print("TIPO MEAN :" + str(type(mean)))
        #print("MEAN :" + str(mean))
        #print("kont :" + str(kont))
        #print("MEAN 0:" + str(mean[0]))
        #print("MEAN 1:" + str(mean[1]))
        #print("MEAN 2:" + str(mean[2]))
        #if len(approx) == 4 and 80<mean[0] and 80<mean[1] and 80<mean[2]:
        if 80<mean[0] and 80<mean[1] and 80<mean[2]:    # 3 matrices
            if first==1:
                screenCnt = approx # la matricula (?)
                first=0
                #print(screenCnt)
            #cv2.drawContours(img, [approx], -1, (0, 0, 255), 3)
            #break

    cv2.imshow('draw_contour',img)
    if screenCnt is None:
        detected = 0
        print ("No contour detected")
    else:
        detected = 1
        #temp = np.zeros_like(gray)
        #cv2.drawContours(temp, [screenCnt], 0, (255,255,255), -1)
        #mask = np.zeros(img, np.uint8)
        #cv2.drawContours(mask, c, -1, 255, -1)
        #mean = cv2.mean(img, temp)
        #print("MEAN AUKERATUTAKOA:" + str(mean))
        #cv2.imshow('Countours',img)
        #cv2.waitKey(0)

    if detected == 1:
        print(screenCnt)
        cv2.drawContours(img, [screenCnt], -1, (0, 0, 255), 3)

        mask = np.zeros(gray.shape,np.uint8)
        new_image = cv2.drawContours(mask,[screenCnt],0,255,-1,)
        new_image = cv2.bitwise_and(img,img,mask=mask)

        (x, y) = np.where(mask == 255)
        (topx, topy) = (np.min(x), np.min(y))
        (bottomx, bottomy) = (np.max(x), np.max(y))
        Cropped = gray[topx:bottomx+1, topy:bottomy+1]
        # se puede girar

        text = pytesseract.image_to_string(Cropped, config='--psm 10') # configuracion, por defecto ingles idioma

        print("Detected license plate Number is:", text)

        matricula=""
        for i in range(1,len(text)):
            #print(matricula)
            if text[i].isnumeric() or text[i].isalpha():
                matricula=matricula + (text[i])
            
        print("Detected license plate Number in file " + file+": ",matricula)
        #img = cv2.resize(img,(500,300))


        #Cropped = cv2.resize(Cropped,(400,200))
        #cv2.imshow('car',img)
        cv2.imshow('Cropped',Cropped)

        cv2.waitKey(0)
        cv2.destroyAllWindows()