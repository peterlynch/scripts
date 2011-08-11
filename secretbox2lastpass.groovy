import au.com.bytecode.opencsv.CSVWriter

IN_FILE = '/app/groovy/sb_export.xml'


def Secretbox = new XmlParser().parse(IN_FILE)
def Groups  =Secretbox.Groups
assert Groups.size() == 1

// Web Sites secrets
def siteSecrets = Groups.Group.Children.Group[3].Secrets.Secret
def grouping = 'Imports'

def lines = new ArrayList<String>()
lines.add("grouping#name#url#username#password#extra".split("#"));

for (secret in siteSecrets) {
    def url, username, password, name, extra = ''
    name = secret.Name.text()
    note = secret.Note.text()
    
    // fields have the rest of the data
    def Fields = secret.Fields.Field
    
    for (field in Fields){
        def nameValue = field.Name.text()
        def textValue = field.Text.text()
        //println nameValue
        if (nameValue == 'Login'){
            username = textValue
        } else if ( nameValue == 'URL' ) {
            url = textValue
        } else if ( nameValue == 'Password' ) {
            password = textValue
        } else { 
            if ( textValue.trim() != '' ) {
                extra += "== ${nameValue}\n${textValue}\n"
            }
        }    
    }
    
    if( note.trim() != '' ) {
        extra += "== Note\n${note}\n"
    }
    
    if ( url.trim() == '' ) {
        url = name
    }
    if ( name.trim() == '' ) {
        name = url
        println "WARNING: found blank name, naming it $url"
    }
    
    lines.add([grouping,name,url,username,password,extra] as String[])
    
}


// setup write
OUTPUT_FILE = '/app/groovy/export.csv'
CSVWriter writer = new CSVWriter(new FileWriter(OUTPUT_FILE));

for (line in lines){
   // println line.getClass().getName()
    writer.writeNext(line);
   //println line
}

writer.close();





 

