import scrapy
from scrapy.crawler import CrawlerProcess

class DGTSpider(scrapy.Spider):
    name = 'dgt_spider'
    
    def __init__(self, matricula=None, *args, **kwargs):
        super(DGTSpider, self).__init__(*args, **kwargs)
        self.matricula = matricula
        self.start_urls = [f'https://sede.dgt.gob.es/es/vehiculos/distintivo-ambiental/?accion=1&matriculahd=&matricula={self.matricula}&submit=Consultar#']

    def parse(self, response):
        distintivo = response.xpath('//div[@id="resultadoBusqueda"]/div/div/p/strong/text()').extract_first()
        print(distintivo)

if __name__ == "__main__":
    import sys
    process = CrawlerProcess()
    process.crawl(DGTSpider, matricula=sys.argv[1])
    process.start()
