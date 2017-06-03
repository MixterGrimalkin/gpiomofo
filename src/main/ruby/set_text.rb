require 'net/http'

app_uri = 'http://192.168.0.71:8001/gpiomofo/'

Net::HTTP.post_form(URI(app_uri+'heading'), [ARGV[0]])
Net::HTTP.post_form(URI(app_uri+'date'), [ARGV[1]])
Net::HTTP.post_form(URI(app_uri+'description'), [ARGV[2]])
