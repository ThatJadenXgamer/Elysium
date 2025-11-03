// typst document about my implementations

#import "@preview/chronos:0.2.1"

= Dodgeroll Sequence Diagrams
== Option A
#chronos.diagram({
  import chronos: *
  _par("P", display-name: "Player")
  _par("AC", display-name: "Client (ATT)")
  _par("AS", display-name: "Server (ATT)")
  _par("ES", display-name: "Server (Elysium)")
  _par("EC", display-name: "Client (Elysium)")

  _seq("P", "AC", comment: "press g")
  _seq("AC", "AS", comment: "send input packet")
  _alt(
    "not transformed", {
      _seq("AS", "ES", comment: "trigger dodgeroll")
      _seq("ES", "ES", comment: "process dodgeroll")
      _seq("ES", "EC", comment: "send dodgeroll packet")
      _seq("EC", "EC", comment: "play animation", flip: true)
    }
  )
})

== Option B
#chronos.diagram({
  import chronos: *
  _par("P", display-name: "Player")
  _par("AC", display-name: "Client (ATT)")
  _par("EC", display-name: "Client (Elysium)")
  _par("ES", display-name: "Server (Elysium)")
  _par("AS", display-name: "Server (ATT)")

  _sep("server startup")

  _seq("AS", "ES", comment: "enable dodging")

  _sep("player join")

  _seq("ES", "EC", comment: "enable dodging")

  _sep("gameplay")

  _seq("P", "AC", comment: "press key")
  _alt(
    "not transformed", {
      _seq("AC", "EC", comment: "trigger dodge")
      _alt(
        "dodging enabled", {
          _seq("EC", "EC", comment: "animation + movement")
          _seq("EC", "ES", comment: "send dodge packet")
          _seq("ES", "ES", comment: "movement + invulnerability", flip: true)
        }
      )
    }
  )
})
