
# coding: utf-8

# In[1]:


from bs4 import BeautifulSoup
import os.path as ospath
import os as os

HEADERS = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX cidoc: <http://erlangen-crm.org/170309/>\nPREFIX vismo: <http://visit.de/ontologies/vismo/>"

# get the current script path.
here = ospath.dirname(ospath.realpath('./'))
subdir = "templates"

# Create a subdirectory, if not existing
if(not ospath.isdir(ospath.join(here, subdir))):
    os.mkdir(ospath.join(here, subdir))


# In[2]:


def preprocess(soup):
    # Change the "name" tags to "pathName"
    names = soup.find_all('name')
    
    for name in names:
        name.name = 'pathName'
        
    # Remove line breaks after some <datatype_property>, <x> and <y> elements
    yPaths = soup.find_all('x')
    for path in yPaths:
        if path.get_text().endswith('\n'):
            path.string.replace_with(path.get_text()[:-1])
            
    yPaths = soup.find_all('y')
    for path in yPaths:
        if path.get_text().endswith('\n'):
            path.string.replace_with(path.get_text()[:-1])
            
    datatypePaths = soup.find_all('datatype_property')
    for path in datatypePaths:
        if path.get_text().endswith('\n'):
            path.string.replace_with(path.get_text()[:-1])


# In[3]:


def createTemplate(groupName, paths):
    query = ''
    
    query += HEADERS + "\n"
    
    select = 'SELECT ?x ?type '
    
    where = 'WHERE { ?x rdf:type ?type .'
    
    optionals = ''
    
    queryFilter = "FILTER regex(str(?x), \"^ADD_ID_HERE$\", \"\")}"
    
    groupBy = 'GROUP BY ?x ?type '
    
    # Find paths with and without sub-groups
    simplePaths = []
    subGroupPaths = []
    for path in paths:
        if(path.group_id.get_text() == groupName.lower() and path.is_group.get_text() == '0'):
            simplePaths.append(path)
        elif(path.group_id.get_text() == groupName.lower() and path.is_group.get_text() == '1'):
            subGroupPaths.append(path)
       
    # Counter for termporary variables
    counter = 0
    # Work simple paths
    for path in simplePaths:
        identifier = '?' + path.id.get_text()
        select += '(group_concat(distinct ' + identifier + ';separator=\",\") as ' + identifier + 's) '
        
        # Presumably not needed
        #groupBy += identifier + ' '
        
        counter += 1
        
        # Check for path length
        if(len(path.path_array.contents) == 3):
            # 1-step Path, Ending in a Property
            optionalPart = 'OPTIONAL {\n\t?x <' + path.datatype_property.get_text() + '> ' + identifier + '}\n'
            
            optionals += optionalPart
        elif(len(path.path_array.contents) == 7):
            if(path.datatype_property.get_text() == 'empty'):
                # 3-step Path, Ending in a Reference
                optionalPart = 'OPTIONAL {\n\t?x <' + path.path_array.y.get_text() + '> ' + identifier + ' .\n\t' + identifier + ' rdf:type <' + path.path_array.find_all('x')[1].get_text() + '> . }\n'
                
                optionals += optionalPart
            else:
                # 3-step Path, Ending in a Property
                optionalPart = 'OPTIONAL {\n\t?x <' + path.path_array.y.get_text() + '> ?y' + str(counter) + ' .\n\t?y' + str(counter) + ' rdf:type <' + path.path_array.find_all('x')[1].get_text() + '> .\n\t?y' + str(counter) + ' <' + path.datatype_property.get_text() + '> ' + identifier + ' . }\n'
                
                optionals += optionalPart
        elif(len(path.path_array.contents) == 11):  
            if(path.datatype_property.get_text() == 'empty'):
                # 5-step Path, Ending in a Reference
                optionalPart = ('OPTIONAL {\n\t?x <' + path.path_array.find_all('y')[0].get_text() + '> ' + '?y' + str(counter) + ' .\n'
                                + '\t' + '?y' + str(counter) + ' rdf:type <' + path.path_array.find_all('x')[1].get_text() + '> .\n'
                                + '\t' + '?y' + str(counter) + ' <' + path.path_array.find_all('y')[1].get_text() + '> ' + identifier + ' .\n'
                                + '\t' + identifier + ' rdf:type <' + path.path_array.find_all('x')[2].get_text() + '> . }\n')
                
                optionals += optionalPart
            else:
                # 5-step Path, Ending in a Property
                optionalPart = ('OPTIONAL {\n\t?x <' + path.path_array.find_all('y')[0].get_text() + '> ' + '?y' + str(counter) + ' .\n'
                                + '\t' + '?y' + str(counter) + ' rdf:type <' + path.path_array.find_all('x')[1].get_text() + '> .\n'
                                + '\t' + '?y' + str(counter) + ' <' + path.path_array.find_all('y')[1].get_text() + '> ?y' + str(counter + 1) + ' .\n'
                                + '\t ?y' + str(counter + 1) + ' <' + path.datatype_property.get_text() + '> '  + identifier + ' . }\n')
                
                counter += 1
                optionals += optionalPart
    for path in subGroupPaths:
        identifier = '?' + path.id.get_text()
        select += '(group_concat(distinct ' + identifier + ';separator=\",\") as ' + identifier + 's) '
        
        counter += 1
        
        optionalPart = 'OPTIONAL {\n\t?x <' + path.path_array.y.get_text() + '> ' + identifier + ' .\n\t' + identifier + ' rdf:type <' + path.path_array.find_all('x')[1].get_text() + '> . }\n'
        
        optionals += optionalPart
        
        # Create sub-templates for the sub-groups of a top-level group 
        # Find all paths that are associated with the subgroup
        subGroupId = path.id.get_text()
        
        createTemplate(subGroupId, paths)
        
        #currentGroupPaths = []
        #for subPath in paths:
        #    if(subPath.group_id.get_text() == subGroupId):
        #        currentGroupPaths.append(subPath)
        
        
        
        # Check subgroups for subgroups!
            
    query += select + "\n" + where + '\n' + optionals + "\n" + queryFilter + "\n" + groupBy
    
    # Write to file
    filename = groupName + ".txt"
    filepath = ospath.join(here, subdir, filename)

    try:
        f = open(filepath, 'w')
        #templateContent = createTemplate(group, paths)
        f.write(query)
        f.close()
    except IOError:
        print('Wrong path provided')


# In[4]:


infile = open("paths.xml","r")
contents = infile.read()
soup = BeautifulSoup(contents,'xml')

# Preprocess the XML
preprocess(soup)

# Find all paths
paths = soup.find_all('path')

# Find all top-level groups
groups = []
for path in paths:
    if path.is_group.get_text() == '1' and path.group_id.get_text() == '0':
        groups.append(path.pathName.get_text())
        
# Write to template files
for group in groups:
    createTemplate(group, paths)

