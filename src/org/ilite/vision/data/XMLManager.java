package org.ilite.vision.data;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class XMLManager {
    
    public static Object read(File file, Class cls) throws JAXBException {
        return JAXBContext.newInstance(cls).createUnmarshaller().unmarshal(file);
    }
    
    public static void write(File file, Object object) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
 
        jaxbMarshaller.marshal(object, file);
        jaxbMarshaller.marshal(object, System.out);
    }
}
