require 'rubygems'
require 'osc-ruby'
require 'osc-ruby/em_server'
require 'unimidi'

@server = OSC::EMServer.new( 5000 )

@server.add_method '/black' do | message |
  midi_value = message.to_a[0]
  UniMIDI::Output.first.puts(0xB0, 57, midi_value)
  # percentage = ((midi_value/255.0)*100).round()
  # puts "[" + ("#"*percentage) + ("-"*(100-percentage)) + "]"
  # UniMIDI::Output.first.puts(0x90, midi_value, 100)
end

puts UniMIDI::Output.first

while true do
  @server.run
end




