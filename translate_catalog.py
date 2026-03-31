import xml.etree.ElementTree as ET
import urllib.request
import urllib.parse
import json
import time

def translate(text):
    if not text: return ""
    try:
        url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ja&dt=t&q=" + urllib.parse.quote(text)
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        response = urllib.request.urlopen(req)
        data = json.loads(response.read().decode('utf-8'))
        return "".join([x[0] for x in data[0]])
    except Exception as e:
        return text

def process_xml(file_path):
    print("Reading XML...")
    tree = ET.parse(file_path)
    root = tree.getroot()
    
    ET.register_namespace('xml', 'http://www.w3.org/XML/1998/namespace')
    ns = '{http://www.w3.org/XML/1998/namespace}lang'
    
    def translate_node(parent, tag):
        en_elem = None
        for child in parent.findall(tag):
            if child.get(ns) == 'en-US':
                en_elem = child
            elif child.get(ns) == 'ja-JP':
                return # Already exists
        
        if en_elem is not None and en_elem.text:
            ja_text = translate(en_elem.text)
            
            ja_elem = ET.Element(tag)
            ja_elem.set(ns, 'ja-JP')
            ja_elem.text = ja_text
            
            # Find the last element with this tag
            last_idx = -1
            for i, child in enumerate(list(parent)):
                if child.tag == tag:
                    last_idx = i
            
            if last_idx != -1:
                parent.insert(last_idx + 1, ja_elem)
            else:
                parent.append(ja_elem)
            time.sleep(0.1)

    print("Translating shipping methods...")
    for method in root.findall('.//shipping-method'):
        translate_node(method, 'name')

    print("Translating categories...")
    for cat in root.findall('.//category'):
        translate_node(cat, 'name')
        for subcat in cat.findall('.//sub-category'):
            translate_node(subcat, 'name')

    print("Translating products...")
    products = root.findall('.//product')
    for i, prod in enumerate(products):
        translate_node(prod, 'name')
        translate_node(prod, 'short-description')
        translate_node(prod, 'long-description')
        if (i+1) % 10 == 0:
            print(f"Processed {i+1} products...")

    print("Writing XML...")
    tree.write(file_path, encoding='utf-8', xml_declaration=True)
    print("Done!")

if __name__ == "__main__":
    process_xml('src/main/resources/data/catalog-import.xml')
