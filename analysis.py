
import json
import matplotlib.pyplot
import os
import random
import subprocess
import time

DEBUG_PRINT  = False

DOMAINS_DIR  = "domains/"
LOGS_DIR     = "logs/"
PARTIES_DIR  = "parties/"
RESULTS_DIR  = "results/"
SESSIONS_DIR = "sessions/"

domains = {}
print(f'No registered domains, starting search')
for dldit in os.listdir(DOMAINS_DIR):
    dldfp = os.path.join(DOMAINS_DIR, dldit)
    if os.path.isdir(dldfp):
        if DEBUG_PRINT:
            print(f'- Found domain {dldfp}')
        domains[dldfp] = []
        for pldit in os.listdir(dldfp):
            if dldit == pldit.split('.')[0]:
                continue
            pldfp = os.path.join(dldfp, pldit).replace("\\","/")
            if DEBUG_PRINT:
                print(f'-- Found profile {pldfp}')
            domains[dldfp].append(f'file:{pldfp}')

def randomDomain ( ) :
    domain = random.choice(list(domains.keys()))
    print(f'- Domain: {domain}')
    return domain
parties = [
    {"partyref": f'classpath:ai2020.group6.parties.MAExponential', "parameters": { "e": 0.3 }},
    {"partyref": f'classpath:geniusweb.exampleparties.boulware.Boulware', "parameters": {}},
    {"partyref": f'classpath:geniusweb.exampleparties.conceder.Conceder', "parameters": {}},
    {"partyref": f'classpath:geniusweb.exampleparties.hardliner.Hardliner', "parameters": {}},
    {"partyref": f'classpath:geniusweb.exampleparties.linear.Linear', "parameters": {}},
    {"partyref": f'classpath:geniusweb.exampleparties.randomparty.RandomParty', "parameters": {}},
    {"partyref": f'classpath:geniusweb.exampleparties.timedependentparty.TimeDependentParty', "parameters": {}}
]
def randomParty ( ) :
    party = random.choice(parties)
    if DEBUG_PRINT:
        print(f'- Party: {party["partyref"]}, Parameters: {party["parameters"]}')
    return party
def randomProfile ( domain ) :
    profile = random.choice(domains[domain])
    if DEBUG_PRINT:
        print(f'- Profile: {profile}')
    return profile

def calculateUtility ( profile, bid ) :
    if DEBUG_PRINT:
        print(f'Profile: {profile}')
    if DEBUG_PRINT:
        print(f'Bid: {bid}')
    util = 0

    issueValues    = bid["issuevalues"]
    issueUtilities = profile["LinearAdditiveUtilitySpace"]["issueUtilities"]
    issueWeights   = profile["LinearAdditiveUtilitySpace"]["issueWeights"]

    for issue, utils in issueUtilities.items(): #:
        if not issue in issueValues:
            continue
        if 'discreteutils' in utils:
            v = issueValues[issue]
            util += issueWeights[issue] * utils['discreteutils']['valueUtilities'][v]
        if 'numberutils' in utils:
            v = issueValues[issue]
            lu = utils['numberutils']['lowUtility']
            hu = utils['numberutils']['highUtility']
            lv = utils['numberutils']['lowValue']
            hv = utils['numberutils']['highValue']
            dist = (v-lv) / (hv - lv)
            util += issueWeights[issue] * (lu + (hu - lu) * dist)

    return util

def generateSession ( name ) :
    print(f'Generating session {name}')

    with open(f'{SESSIONS_DIR}{name}.json', 'w') as f:
        json.dump({
            "MOPACSettings": {
                "participants" : [
                    {
                        "party": randomParty(),
                        "profile": domains["domains/party"][0]
                    }, {
                        "party": randomParty(),
                        "profile": domains["domains/party"][1]
                    }, {
                        "party": randomParty(),
                        "profile": domains["domains/party"][2]
                    }, {
                        "party": randomParty(),
                        "profile": domains["domains/party"][3]
                    }
                ],
                "deadline": {
                    "deadlinerounds": {
                        "rounds": 10,
                        "durationms": 10000
                    }
                },
                "votingevaluator": {
                    "LargestAgreementsLoop": {}
                }
            }
        }, f, sort_keys=True, indent=4)

def runSession ( name, sleeptime = 1 ) :
    print(f'Running session {name}')
    with open(f'{LOGS_DIR}{name}.txt','w') as f :
        p = subprocess.Popen([
            'java',
            '-cp',
            '.'+
            ';simplerunner-1.5.5-jar-with-dependencies.jar'+
            f';{PARTIES_DIR}maexponential-1.5.5-jar-with-dependencies.jar'+
            f';{PARTIES_DIR}mamirroredexponential-1.5.5-jar-with-dependencies.jar'+
            f';{PARTIES_DIR}mapowereightedexponential-1.5.5-jar-with-dependencies.jar'+
            f';{PARTIES_DIR}boulware-1.5.5.jar'+
            f';{PARTIES_DIR}conceder-1.5.5.jar'+
            f';{PARTIES_DIR}hardliner-1.5.5.jar'+
            f';{PARTIES_DIR}linear-1.5.5.jar'+
            f';{PARTIES_DIR}randomparty-1.5.5.jar'+
            f';{PARTIES_DIR}timedependentparty-1.5.5.jar',
            'geniusweb.simplerunner.NegoRunner',
            f'{SESSIONS_DIR}{name}.json'
        ], stdout=f, stderr=f)
        time.sleep(sleeptime)
        p.kill()

analysis = {}
def analyseSession ( name ) :
    print(f'Analysing session {name}')
    with open(f'{LOGS_DIR}{name}.txt','r') as f :
        lines = f.readlines()
        datastore = json.loads(lines[-2].split("INFO:protocol ended normally: ")[1])
        for id in datastore["MOPACState"]["partyprofiles"]:
            sid = f'{"".join([c for c in id.split("_")[-2] if c.isupper()])}_{datastore["MOPACState"]["partyprofiles"][id]["party"]["parameters"]}'
            if not sid in analysis:
                if DEBUG_PRINT:
                    print(f'Unknown id {sid}, adding to analysis')
                analysis[sid] = []
            if not id in datastore["MOPACState"]["phase"]["OptInPhase"]["partyStates"]["agreements"]:
                analysis[sid].append(0)
                continue
            bid = datastore["MOPACState"]["phase"]["OptInPhase"]["partyStates"]["agreements"][id]
            fp = datastore["MOPACState"]["partyprofiles"][id]["profile"]
            with open(fp.split(":")[1],'r') as f :
                profile = json.load(f)
                utility = calculateUtility(profile, bid)
                if DEBUG_PRINT:
                    print(f'{sid}: {calculateUtility(profile, bid)}')
                analysis[sid].append(utility)

def saveanalysis ( filename, analysis ) :
    print("Saving Analysis")
    with open(filename, 'w') as f:
        json.dump(analysis, f)

def boxplotanalysis ( filename ) :
    with open(filename, 'r') as f:
        analysis = json.load(f)

    x = []
    i = []
    n = []
    for k, v in sorted(analysis.items()):
        sid = f'{"".join([c for c in k.split("_")[0] if c.isupper()])}_{k.split("_")[1]}'
        x.append(v)
        i.append(len(i)+1)
        n.append(sid)
    matplotlib.pyplot.boxplot(x, showmeans=True)
    matplotlib.pyplot.xticks(i, n)
    matplotlib.pyplot.show()

for i in range(0, 100):
    generateSession(f's{i}')

for i in range(0, 100):
    runSession(f's{i}')

for i in range(0, 100):
    analyseSession(f's{i}')

time.sleep(1)

saveanalysis("sanalysis.txt", analysis)

boxplotanalysis ( "sanalysis.txt" )
