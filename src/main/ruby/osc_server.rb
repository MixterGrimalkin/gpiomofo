require 'rubygems'
require 'osc-ruby'
require 'osc-ruby/em_server'

@server = OSC::EMServer.new( 5000 )

@server.add_method '/black' do | message |
  percentage = ((message.to_a[0]/255.0)*100).round()
  puts "[" + ("#"*percentage) + ("-"*(100-percentage)) + "]"
end

# @server.add_method '/red' do | message |
#   puts "#{message.ip_address}:#{message.ip_port} -- #{message.address} -- #{message.to_a}"
# end

while true do
  @server.run
end