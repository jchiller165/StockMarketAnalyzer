import csv

def edit_nasdaq_csv(path1, path2):
    with open(path1,"rb") as source:
        rdr= csv.reader( source )
        with open(path2,"wb") as result:
            wtr= csv.writer( result )
            for r in rdr:
            	r[1] = r[1].replace(', ', ' ') 
                wtr.writerow( (r[0], r[1], r[5], r[6]) )
                
def edit_nyse_csv(path1, path2):
    with open(path1,"rb") as source:
        rdr= csv.reader( source )
        with open(path2,"wb") as result:
            wtr= csv.writer( result )
            for r in rdr:
            	r[1] = r[1].replace(', ', ' ') 
                wtr.writerow( (r[0], r[1], r[5], r[6]) )

path1 = "/Users/justin_hiller/eclipse-workspace/StockMarketAnalyzer-1/src/main/resources/data/symbols/nasdaq.csv"
path2 = "/Users/justin_hiller/eclipse-workspace/StockMarketAnalyzer-1/src/main/resources/data/symbols/nasdaq_symbols.csv"
path3 = "/Users/justin_hiller/eclipse-workspace/StockMarketAnalyzer-1/src/main/resources/data/symbols/nyse.csv"
path4 = "/Users/justin_hiller/eclipse-workspace/StockMarketAnalyzer-1/src/main/resources/data/symbols/nyse_symbols.csv"

edit_nasdaq_csv(path1, path2)
edit_nyse_csv(path3, path4)