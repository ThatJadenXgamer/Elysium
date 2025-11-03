// typst document about my implementations

#import "@preview/chronos:0.2.1"

#set page(
  paper: "a3",
  height: auto
)

= Dodgeroll Sequence Diagrams
== Option A
#chronos.diagram({
  import chronos: *
  _par("P", display-name: "Player")
  _par("AC", display-name: "Client (ATT)")
  _par("AS", display-name: "Server (ATT)")
  _par("ES", display-name: "Server (Elysium)")
  _par("EC", display-name: "Clients (Elysium)")

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
  _par("EOC", display-name: "Multiplayer Clients (Elysium)")

  _sep("server startup")

  _seq("AS", "ES", comment: "enable dodging")

  _sep("player join")

  _seq("ES", "EC", comment: "send config package")

  _sep("gameplay")

  _seq("P", "AC", comment: "press key")
  _alt(
    "not transformed", {
      _seq("AC", "EC", comment: "trigger dodgeroll")
      _alt(
        "dodging enabled", {
          _seq("EC", "EC", comment: "animation + movement")
          _seq("EC", "ES", comment: "send dodgeroll packet")
          _seq("ES", "ES", comment: "movement + invulnerability")
          _seq("ES", "EOC", comment: "send animation packet")
          _seq("EOC", "EOC", comment: "play animation")
        }
      )
    }
  )
})

== Hybrid
#chronos.diagram({
  import chronos: *
  _par("P", display-name: "Player")
  _par("AC", display-name: "Client (ATT)")
  _par("EC", display-name: "Client (Elysium)")
  _par("ES", display-name: "Server (Elysium)")
  _par("AS", display-name: "Server (ATT)")
  _par("EMC", display-name: "Multiplayer Clients (Elysium)")

  _sep("server startup")
  _seq("AS", "ES", comment: "enable dodging")

  _sep("gameplay")
  _seq("P", "AC", comment: "press key")
  _alt(
    "not transformed", {
      _seq("AC", "EC", comment: "trigger dodgeroll")
      _seq("EC", "EC", comment: "animation + movement")
      _seq("EC", "ES", comment: "send dodgeroll packet")
      _alt(
        "dodging enabled", {
          _seq("ES", "ES", comment: "movement + invulnerability")
          _sync({
            _seq("ES", "EMC", comment: "send animation packet")
            _seq("ES", "EC", start-tip: "o", comment: "send animation packet")
          })
          _sync({
            _seq("EMC", "EMC", comment: "play animation")
            _seq("EC", "EC", end-tip: "x", comment: "ignore packet")
          })
        }
      )
    }
  )
})
