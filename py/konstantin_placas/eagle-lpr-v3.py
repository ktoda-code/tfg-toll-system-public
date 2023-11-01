import sys
import cv2
import imutils
import base64
import numpy as np
import pytesseract
from pathlib import Path

TESSERACT_CMD = r"c:\Program Files\Tesseract-OCR\tesseract.exe"
pytesseract.pytesseract.tesseract_cmd = TESSERACT_CMD


def decode_base64_to_image(base64_string):
    decoded_data = base64.b64decode(base64_string)
    np_data = np.frombuffer(decoded_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_COLOR)
    return img


def load_and_preprocess_image(base64_string):
    img = decode_base64_to_image(base64_string)
    # Step 1
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    # Step 2
    blur = cv2.GaussianBlur(gray, (5, 5), 0)
    return img, blur


def find_plate_contour(blurred_img):
    # Step 3
    edged = cv2.Canny(blurred_img, 100, 150)
    contours = cv2.findContours(
        edged.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
    )
    contours = imutils.grab_contours(contours)
    contours = sorted(contours, key=cv2.contourArea, reverse=True)[:40]

    for c in contours:
        peri = cv2.arcLength(c, True)
        approx = cv2.approxPolyDP(c, 0.018 * peri, True)
        temp = np.zeros_like(blurred_img)
        cv2.drawContours(temp, [approx], 0, (255, 255, 255), -1)
        mean = cv2.mean(img, temp)

        if all(val > 80 for val in mean[:3]):
            return approx
    return None


def extract_license_text(roi_gray):
    # Step 4
    text = pytesseract.image_to_string(roi_gray, config="--psm 10")

    # Filtering the text
    matricula = ""
    for i in range(1, len(text)):
        if text[i].isnumeric() or text[i].isalpha():
            matricula += text[i]

    return matricula


if __name__ == "__main__":
    # encoded_string = sys.argv[1]
    with open(sys.argv[1], "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read()).decode('utf-8')
    # encoded_string = base64.b64encode(sys.argv[1].read()).decode('utf-8')

    img, blur = load_and_preprocess_image(encoded_string)

    plate_contour = find_plate_contour(blur)
    if plate_contour is None:
        print("No contour detected")
    else:
        mask = np.zeros(blur.shape, np.uint8)
        new_image = cv2.drawContours(mask, [plate_contour], 0, 255, -1)
        new_image = cv2.bitwise_and(img, img, mask=mask)
        (x, y) = np.where(mask == 255)
        roi = blur[np.min(x) : np.max(x) + 1, np.min(y) : np.max(y) + 1]
        license_text = extract_license_text(roi)
        print(license_text)

        # cv2.imshow('Cropped', roi)
        cv2.waitKey(0)
        cv2.destroyAllWindows()
