  HttpPost httpPost = new HttpPost("http://101.251.236.60:18002/send.do");
            String msStr=URLEncoder.encode(sendcontent);
        // ����post����
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("ua", this.account));
        parameters.add(new BasicNameValuePair("pw", this.password));
        parameters.add(new BasicNameValuePair("mb", mobile));
        parameters.add(new BasicNameValuePair("ms",msStr));
        parameters.add(new BasicNameValuePair("ex", messageSend.getSrcId()));
        parameters.add(new BasicNameValuePair("dm", ""));
��ַд��������δ�ģ�msStr��urlEncoderת��