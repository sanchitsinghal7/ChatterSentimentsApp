public class InstantSentimentAnalysisHelper {

    @future(callout=true)
    public static void analyzeFeedSentiment(List<String> feedItemList){
        
        List<Sentimental_Topic__c> sentiTopicList = [select name, Positives__c,Negatives__c from Sentimental_Topic__c];
        List<Sentimental_Topic__c> sentiTopicUpdates = new List<Sentimental_Topic__c>();
        System.debug('Sure Shot Comment!!!');
        
        for(String fi: feedItemList){
            if(fi != null){
                System.debug('fi: ' + fi);
                for(Sentimental_Topic__c s: sentiTopicList){
                    if(fi.toUpperCase().contains(s.name.toUpperCase())){
                        System.debug('fi: ' + fi);
                        if(isPositive(fi) == true){
                            sentiTopicUpdates.add(new Sentimental_Topic__c(id=s.id, Positives__c = s.Positives__c+1));
                        }
                        else{
                            sentiTopicUpdates.add(new Sentimental_Topic__c(id=s.id, Negatives__c = s.Negatives__c+1));
                        }
                    }
                }    
            }
        }
        
        update sentiTopicUpdates;
    }        
    
    public static Boolean isPositive(String feedText){    
        String saendpoint = 'http://access.alchemyapi.com/calls/text/TextGetTextSentiment';
        String encoded = EncodingUtil.urlEncode(feedText,'UTF-8');
        httpRequest req = new httpRequest();
        req.setendpoint(saendpoint+'?apikey=0c854d9e78662419a14caa4a0115f17e7b25249b'+
                                '&text='+ encoded +'&outputMode=json');
        req.setMethod('POST');
        req.setHeader('Content-Length','0');
        Http http = new Http();
        HTTPResponse res = http.send(req);
        system.debug('Response: ' + res.getBody());

        //Parse JSON string.
        JSONParser parser = JSON.createParser(res.getBody());
        parser.nextToken();
        while(parser.getcurrentToken() != null){   
            system.debug('parser.nextToken():' + parser.getText());
            if(parser.getText() == 'positive'){
               return true; 
            }
            else if(parser.getText() == 'negative'){
               return false; 
            }
            parser.nextToken();
        }
        return null;
    }

}